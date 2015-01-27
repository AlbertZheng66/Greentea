package com.xt.core.db.po.mapping;

import java.io.InputStream;
import java.util.List;

import org.jdom.Element;

import com.xt.core.utils.StringUtils;
import com.xt.core.utils.XmlHelper;

/**
 * <p>
 * Title: XT框架-持久化部分-Hibernate的配置映射。
 * </p>
 * <p>
 * Description: 将Hibernate的配置文件映射到此类中。
 * 目前，这个文件映射的格式是Hibernate3.0格式。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 郑伟
 * @version 1.0
 * @date 2006-4-15
 */

public class HiberFileConf {

	private String packageName;

	private String className;
	
	//对应的表名称，可以省略（即按照默认规则算）
	private String tableName;  
	
	private Id id;

	private Property[] properties;
	
	public HiberFileConf () {
	}
	
	/**
	 * 负责装载Hibernate映射的属性，但是不负责校验XML的格式，必添属性等信息，由调用其的类校验。
	 * @param xml
	 */
	public void load (InputStream xml) {
		Element root = XmlHelper.getRoot(xml); 
		
		//得到包名
		packageName = root.getAttributeValue("package");
		
		//类节点
		Element classNode = root.getChild("class");
		
		className = classNode.getAttributeValue("name");
		
		tableName = classNode.getAttributeValue("table");
		
		//读取Id(todo)
//		Element idNode = classNode.getChild("id");
		
		//读取所有属性
		List propertyNodes = classNode.getChildren("property");
		if (propertyNodes != null && propertyNodes.size() > 0) {
			properties = new Property[propertyNodes.size()];
			for (int i = 0; i < properties.length; i++) {
				Property p = new Property();
				Element pNode = (Element)propertyNodes.get(i);
				p.setName(pNode.getAttributeValue("name"));
				String excludeStr = pNode.getAttributeValue("exclude");
				p.setExclude(StringUtils.isTrue(excludeStr));
				p.setColumnName(pNode.getAttributeValue("column"));
				p.setNotNull("true".equals(pNode.getAttributeValue("not-null")));
				properties[i] = p;
			}
		}
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public Property[] getProperties() {
		return properties;
	}

	public void setProperties(Property[] properties) {
		this.properties = properties;
	}

	/**
	 * 根据字段名称返回相应的属性值
	 * @param columnName
	 * @return
	 */
	public Property findProperty(String columnName) {
		Property property = null;
		if (properties != null && null != columnName) {
			for (int i = 0; i < properties.length; i++) {
				if (columnName.equals(properties[i].getColumnName())) {
					property = properties[i];
				}
			}
		}
		return property;
	}
	
	/**
	 * 根据属性名称得到其相应的字段名称
	 * @param propertyName 属性名称
	 * @return
	 */
	public String findColumnName(String propertyName) {
		String columnName = null;
		if (properties != null && null != propertyName) {
			for (int i = 0; i < properties.length; i++) {
				if (propertyName.equals(properties[i].getName())) {
					columnName = properties[i].getColumnName();
				}
			}
		}
		return columnName;
	}

	public Id getId() {
		return id;
	}

	public void setId(Id id) {
		this.id = id;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}


/**
 * 以下是Hibernate3.0的一个例子文件，本解析器是基于此文件格式的。
 <?xml version="1.0"?>
<!DOCTYPE hibernate-mapping PUBLIC 
	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
	"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd">
<hibernate-mapping 
	package="org.hibernate.auction">

	<class name="Bid" 
		discriminator-value="N">
		<comment>A bid or "buy now" for an item.</comment>
		
		<id name="id">
			<generator class="native"/>
		</id>
		
		<discriminator type="char">
			<column name="isBuyNow">
				<comment>Y if a "buy now", N if a regular bid.</comment>
			</column>
		</discriminator>
		
		<natural-id>
			<many-to-one name="item"/>
			<property name="amount"/>
		</natural-id>
		
		<property name="datetime" 
				not-null="true" 
				column="`datetime`"/>
		
		<many-to-one name="bidder" 
				not-null="true"/>
		
		<subclass name="BuyNow" 
			discriminator-value="Y"/>
			
	</class>
	
</hibernate-mapping>

<?xml version="1.0"?>
<!DOCTYPE hibernate-mapping PUBLIC
    "-//Hibernate/Hibernate Mapping DTD 2.0//EN"
    "http://hibernate.sourceforge.net/hibernate-mapping-2.0.dtd" >
    
<hibernate-mapping>
<!-- 
    Created by the Middlegen Hibernate plugin 2.1

    http://boss.bekk.no/boss/middlegen/
    http://www.hibernate.org/
-->

<class 
    name="com.qnuse.usboss.po.CfgAcctItem" 
    table="CFG_ACCT_ITEM"
>
    <meta attribute="class-description" inherit="false">
       @hibernate.class
        table="CFG_ACCT_ITEM"
    </meta>
    <meta attribute="extends" inherit="false">BaseHibernateObject</meta>

    <id
        name="acctItemId"
        type="java.lang.String"
        column="ACCT_ITEM_ID"
    >
        <meta attribute="field-description">
           @hibernate.id
            generator-class="assigned"
            type="java.lang.String"
            column="ACCT_ITEM_ID"


        </meta>
        <generator class="assigned" />
    </id>

    <property
        name="acctItemName"
        type="java.lang.String"
        column="ACCT_ITEM_NAME"
        length="32"
    >
        <meta attribute="field-description">
           @hibernate.property
            column="ACCT_ITEM_NAME"
            length="32"
        </meta>    
    </property>
    <property
        name="visible"
        type="java.lang.String"
        column="VISIBLE"
        length="2"
    >
        <meta attribute="field-description">
           @hibernate.property
            column="VISIBLE"
            length="2"
        </meta>    
    </property>
    
    <property
        name="description"
        type="java.lang.String"
        column="DESCRIPTION"
        length="2"
    >
        <meta attribute="field-description">
           @hibernate.property
            column="DESCRIPTION"
            length="64"
        </meta>    
    </property>

    <!-- Associations -->
  
    <!-- bi-directional one-to-many association to CfgAcctRelation -->
    <set
        name="cfgAcctRelations"
        lazy="true"
        inverse="true"
		cascade="none"
    >
        <meta attribute="field-description">
           @hibernate.set
            lazy="true"
            inverse="true"
            cascade="none"

           @hibernate.collection-key
            column="ACCT_ITEM_ID"

           @hibernate.collection-one-to-many
            class="com.qnuse.usboss.po.CfgAcctRelation"
        </meta>
        <key>
            <column name="ACCT_ITEM_ID" />
        </key>
        <one-to-many 
            class="com.qnuse.usboss.po.CfgAcctRelation"
        />
    </set>

</class>
</hibernate-mapping>

 */
