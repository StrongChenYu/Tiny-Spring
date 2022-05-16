package com.csu.springframework.mybatis.builder.xml;

import com.csu.springframework.mybatis.builder.BaseBuilder;
import com.csu.springframework.mybatis.datasource.druid.DruidDataSourceFactory;
import com.csu.springframework.mybatis.io.Resources;
import com.csu.springframework.mybatis.mapping.BoundSql;
import com.csu.springframework.mybatis.mapping.Environment;
import com.csu.springframework.mybatis.mapping.MappedStatement;
import com.csu.springframework.mybatis.mapping.SqlCommandType;
import com.csu.springframework.mybatis.session.Configuration;
import com.csu.springframework.mybatis.transaction.TransactionFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 这个类的目的就是读取XML文件
 * 然后往里面填东西
 * 填入到Configuration这个类里面
 */
public class XMLConfigBuilder extends BaseBuilder {

    private Element root;

    public XMLConfigBuilder(Reader reader) {
        super(new Configuration());
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(new InputSource(reader));
            root = document.getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public Element getRoot() {
        return root;
    }

    public Configuration parse() {
        try {
            environmentsElement(root.element("environments"));
            mapperElement(root.element("mappers"));

        } catch (Exception e) {
            throw new RuntimeException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
        }

        return configuration;
    }

    public void environmentsElement(Element context) throws Exception {
        // 这个会去获取节点下的属性是default
        // default表示选择的环境
        String environmentStr = context.attributeValue("default");
        List<Element> environments = context.elements("environmentStr");

        for (Element element : environments) {
            String id = element.attributeValue("id");

            // 如果某个环境配置等于default提到的环境配置，就生成Environment环境
            if (environmentStr.equals(id)) {
                // 解析<transactionManager type="JDBC"/>
                String transactionManagerStr = element.element("transactionManager").attributeValue("type");
                Class<Object> transactionManagerClass = typeAliasRegistry.resolveAlias(transactionManagerStr);
                TransactionFactory transactionFactory = (TransactionFactory) transactionManagerClass.newInstance();

                // 解析DataSource
                String dataSourceStr = element.element("dataSource").attributeValue("type");
                Class<Object> dataSourceClass = typeAliasRegistry.resolveAlias(dataSourceStr);
                DruidDataSourceFactory druidDataSourceFactory = (DruidDataSourceFactory) dataSourceClass.newInstance();
                DataSource dataSource = druidDataSourceFactory.getDataSource();

                // 建造者模式构造一个environment
                Environment environment = new Environment.Builder(id)
                                                    .dataSource(dataSource)
                                                    .transactionFactory(transactionFactory)
                                                    .build();

                configuration.setEnvironment(environment);
            }
        }
    }

    public void mapperElement(Element mappers) throws IOException, DocumentException, ClassNotFoundException {
        // get每一个mapper节点
        List<Element> mapperList = mappers.elements("mapper");
        for (Element mapperElement : mapperList) {
            // 去get节点下的resource节点
            String resourceStr = mapperElement.attributeValue("resource");
            // 根据resource节点中提到的值去读取具体的文件
            Reader reader = Resources.getResourceAsReader(resourceStr);

            SAXReader saxReader = new SAXReader();

            /*
             * <mapper namespace="cn.bugstack.mybatis.test.dao.IUserDao">
             *  ......
             * </mapper>
             */

            Document document = saxReader.read(new InputSource(reader));
            // 这个rootElement就是mapper节点
            Element rootElement = document.getRootElement();

            // 把那么类添加进去
            String interfaceName = rootElement.attributeValue("namespace");
            configuration.addMapper(Resources.classForName(interfaceName));

            // 然后把每个类的方法注入到configuration的mappedStatements里面
            // 处理select节点
            List<Element> selects = rootElement.elements("select");
            for (Element select : selects) {
                // 获取select中的各个属性值

                /*
                 *     <select id="queryUserInfoById" parameterType="java.lang.Long" resultType="cn.bugstack.mybatis.test.po.User">
                 *         SELECT id, userId, userName, userHead
                 *         FROM user
                 *         where id = #{id}
                 *     </select>
                 */
                String id = select.attributeValue("id");
                // 多个参数怎么办
                String parameterType = select.attributeValue("parameterType");
                String resultType = select.attributeValue("resultType");
                String sql = select.getText();

                Map<Integer, String> parameterMapping = new HashMap<>();
                Pattern pattern = Pattern.compile("(#\\{(.*?)})");
                Matcher matcher = pattern.matcher(sql);

                for (int i = 1; matcher.find(); i++) {
                    // (id, 参数名)
                    // id,参数名放进去
                    // 然后把那个地方换为问号？
                    String g1 = matcher.group(1);
                    String g2 = matcher.group(2);
                    parameterMapping.put(i, g2);
                    sql = sql.replace(g1, "?");
                }

                String msId = interfaceName + "." + id;
                // 获取SELECT这个字符串
                String nodeName = select.getName();
                SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));

                BoundSql boundSql = new BoundSql(sql, parameterMapping, parameterType, resultType);

                MappedStatement mappedStatement = new MappedStatement.Builder(configuration, msId, sqlCommandType, boundSql).build();
                configuration.addMappedStatement(mappedStatement);
            }

            // todo: Update
            // todo: Delete
            // todo: insert
        }
    }

}
