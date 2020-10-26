package com.zyy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;

/**
 * @Author: zhouyuyang
 * @Date: 2020/10/26 15:39
 */
public class Application {

    public static void main(String[] args) throws IOException {
        String url = "https://search.jd.com/Search";
        for (int i = 1; i <= 10; i++) {
            Document document = Jsoup.connect(url)
                    .data("keyword", "手机")
                    .data("enc", "utf-8")
                    .data("page", i + "")
                    .timeout(3000)
                    .get();
            Element goodsListElement = document.body().getElementById("J_goodsList");
            Elements itemElement = goodsListElement.getElementsByClass("gl-item");
            for (Element element : itemElement) {
                String image = getElementAttr(element, "data-lazy-img","c:p-img", "t:img" );
                String price = getElementText(element, "c:p-price", "t:i");
                String name = getElementText(element, "c:p-name", "t:em");
                String shop = getElementText(element, "c:p-shop", "t:a");
                System.out.println("商品图片：" + image);
                System.out.println("商品价格：" + price);
                System.out.println("商品名称：" + name);
                System.out.println("商品店铺：" + shop);
                System.out.println("*******************************************************************************");
            }
        }

    }


    private static Element getChildElement(Element parentElement, String... elementTypes){
        String elementType = elementTypes[0];
        Element element = null;
        if (elementType.startsWith("c:")){
            element = parentElement.getElementsByClass(elementType.substring(2)).first();
        }else if (elementType.startsWith("t:")){
            element = parentElement.getElementsByTag(elementType.substring(2)).first();
        }else if (elementType.startsWith("i:")){
            element = parentElement.getElementById(elementType.substring(2));
        }
        if (elementTypes.length == 1 || element == null){
            return element;
        }else{
            return getChildElement(element, Arrays.copyOfRange(elementTypes, 1, elementTypes.length));
        }
    }

    private static String getElementText(Element parentElement, String... elementTypes){
        Element element = getChildElement(parentElement, elementTypes);
        return element == null ? "" : element.text();
    }

    private static String getElementAttr(Element parentElement, String attrKey, String... elementTypes){
        Element element = getChildElement(parentElement, elementTypes);
        return element == null ? "" : element.attr(attrKey);
    }
}
