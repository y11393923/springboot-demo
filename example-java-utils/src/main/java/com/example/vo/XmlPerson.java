package com.example.vo;

import com.example.util.XmlUtil;
import com.google.common.collect.Lists;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @Author: zhouyuyang
 * @Date: 2020/8/6 14:09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XStreamAlias("xml_person")//定义节点别名
public class XmlPerson {
    @XStreamOmitField  //输出XML的时候忽略该属性
    private Integer id;
    @XStreamAlias("username")//定义节点别名
    private String name;
    @XStreamAsAttribute  //该属性不单独显示成XML节点，而是作为属性显示出来
    private String gender;
    private Address address;
    @XStreamImplicit(itemFieldName="phone") //省略集合根节点
    private List<String> phones;
    private Date birthday;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address{
        private String details;
        private String desc;
    }

    @Override
    public String toString() {
        return "XmlPerson{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", address=" + address +
                ", phones=" + phones +
                '}';
    }

    public static void main(String[] args) {
        XmlPerson xmlPerson = XmlPerson.builder().id(1).name("li").gender("男")
                .address(Address.builder().details("广东深圳").desc("来了就是深圳人").build())
                .phones(Lists.newArrayList("123456","abc"))
                .birthday(new Date()).build();
        String xml = XmlUtil.toXml(xmlPerson);
        System.out.println(xml);
        XmlPerson person = XmlUtil.toBean(xml, XmlPerson.class);
        System.out.println(person.toString());

        String compressXml = XmlUtil.toCompressXml(xmlPerson);
        System.out.println(compressXml);
    }
}
