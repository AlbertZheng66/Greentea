/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xt.core.json;

import com.xt.proxy.event.Response;
import junit.framework.TestCase;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author albert
 */
public class JsonBuilderTest extends TestCase {

    public JsonBuilderTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of build method, of class JSonBuilder.
     */
    public void xtestBuild() {
        Object value = new java.sql.Date(0);
        Class valueClass = value.getClass();
        System.out.println("valueClass.isAssignableFrom(Date.class) || valueClass.isAssignableFrom(Calendar.class)="
                + (Object.class.isAssignableFrom(valueClass)));

        System.out.println("build");
//        NewClass serializable = new NewClass();
//        JsonBuilder instance = new JsonBuilder();
//        // String expResult = \"\";
//        String result = instance.build(serializable);
//        System.out.println("result=" + result);
        // assertEquals(expResult, result);

    }
    
    

    /**
     * 测试“枚举字典”的情况
     */
    public void testForEnumDic () {
//        EnumDictionary enumDic = new EnumDictionary(EnumForParserTest.class);
//        JsonBuilder instance = new JsonBuilder();
//        // String expResult = \"\";
//        String result = instance.build(enumDic);
//        System.out.println("result=" + result);
    }

    public void xtestParse() {
        // String str = "{\"child\":{\"__className\":\"com.xt.gt.html.service.ChildClass\",\"string1\":\"aaaaaaaaaaaaaaaaaaaa\",\"map1\":{},\"double1\":333.22,\"int1\":20},\"long1\":200,\"float1\":22.219999313354492,\"__className\":\"com.xt.gt.html.service.NewClass\",\"string1\":\"aaaaaaaaaaaaaaaaaaaa\",\"list\":[],\"map1\":{\"key2\":23456,\"key1\":\"key1\"},\"double1\":333.22,\"int1\":20,\"short1\":12}";
        String str = "{'serviceClassName':'com.xt.gt.demo.service.BookService','methodName':'list','serviceObject':{'className':'com.xt.gt.demo.service.BookService','invokedMethods':[],'fspParameter':{'__className':'com.xt.gt.chr.event.FspParameter','filterGroup':{'__className':'com.xt.gt.chr.event.filter.FilterGroup','filterItems':[{'name':'name','type':'CONTAINS','values':['c']},{'name':'name','type':'CONTAINS','values':['c']}]},'pagination':{'__className':'com.xt.gt.chr.event.Pagination','startIndex':0,'maxRowCount':12,'rowsPerPage':10,'totalCount':-1}}},'type':null,'params':[]}";
        JsonBuilder instance = new JsonBuilder();
        Object obj = instance.parse(str);
        System.out.println("obj=" + ToStringBuilder.reflectionToString(obj));
        //System.out.println("child=" + ToStringBuilder.reflectionToString(((NewClass)obj).getChild()));
    }


      public void xtestParse2() {
        String str = "{\"serviceClassName\":\"com.xt.gt.html.demo.TableService\",\"methodName\":\"list\",\"invokedMethods\":[{\"localMethodName\":\"setFspParameter\",\"localParams\":[{\"pagination\":{\"startIndex\":0,\"totalCount\":-1,\"rowsPerPage\":20},\"filterGroup\":{}}]}],\"params\":[]}";
        JsonBuilder instance = new JsonBuilder();
        Object obj = instance.parse(str);
        System.out.println("obj=" + ToStringBuilder.reflectionToString(obj));
//        System.out.println("child=" + ToStringBuilder.reflectionToString(((NewClass)obj).getChild()));
    }


    static public void main(String[] argv) {
        JsonBuilderTest test = new JsonBuilderTest("");
        test.testForEnumDic();
    }
}
