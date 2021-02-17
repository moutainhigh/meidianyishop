package com.meidianyi.shop.service.foundation.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * 自动国际化翻译器测试
 *
 * @author 郑保乐
 */
public class VoTranslatorTest {

    private PureVo[] voes = new PureVo[5];

    private ListVo listVo = new ListVo();

    private VoTranslator translator;


    @Before
    public void setUp() {
        voes[0] = new PureVo("food");
        voes[1] = new PureVo("digital");
        voes[2] = new PureVo("clean");
        voes[3] = new PureVo("cook");
        voes[4] = new PureVo("drink");
    }


    /**
     * 测试请求头中包含语言字段时的翻译
     */
    @Test
    public void assertPropertyTranslatedZhCN() {
        translateCategoryArray();
    }

    /**
     * 测试多语言 - 英语
     */
    @Test
    public void assertPropertyTranslatedEnUS() {
        PureVo vo = new PureVo("food");
        VoTranslator.translateFields(vo,"en_US");
        assertEquals("FOOD", vo.getName());
    }

    /**
     * 翻译数组中的对象
     */
    private void translateCategoryArray() {
        for (PureVo vo : voes) {
            VoTranslator.translateFields(vo,"zh_CN");
        }
        assertEquals("食品", voes[0].getName());
        assertEquals("数码", voes[1].getName());
        assertEquals("清洁", voes[2].getName());
        assertEquals("厨具", voes[3].getName());
        assertEquals("饮品", voes[4].getName());
    }

    /**
     * 测试出参类中包含 {@code List<String>} 属性的翻译
     */
    @Test
    public void assertListPropertyTranslated() {
        listVo.setList(Arrays.asList("food", "clean", "cook", "digital", "drink"));
        VoTranslator.translateFields(listVo,"zh_CN");
        assertEquals("食品", listVo.getList().get(0));
        assertEquals("清洁", listVo.getList().get(1));
        assertEquals("厨具", listVo.getList().get(2));
        assertEquals("数码", listVo.getList().get(3));
        assertEquals("饮品", listVo.getList().get(4));
    }

    /**
     * 测试 List 的子类型属性以及 List 为 null
     *
     * 不再支持声明 List 的子类型
     */
    @Test
    public void assertSubClassOfListPropertyTranslated() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("food");
        listVo.setArrayList(arrayList);
        try {
            VoTranslator.translateFields(listVo,"zh_CN");
        } catch (Exception ignore) {}
        assertEquals("food", listVo.getArrayList().get(0));
    }

    /**
     * 测试嵌套对象
     */
    @Test
    public void assertNestedObjectPropertyTranslated() {
        NestedPureVo nestedPureVo = new NestedPureVo(voes[0]);
        VoTranslator.translateFields(nestedPureVo,"zh_CN");
        PureVo nestedVo = nestedPureVo.getNestedVo();
        assertEquals("食品", nestedVo.getName());
    }

    /**
     * 测试通过 List 嵌套对象
     */
    @Test
    public void assertListOfObjectPropertyTranslated() {
        List<PureVo> pureVos = Arrays.asList(
                new PureVo("food"),
                new PureVo("clean")
        );
        listVo.setPureVos(pureVos);
        VoTranslator.translateFields(listVo,"zh_CN");
        assertEquals("食品", listVo.getPureVos().get(0).getName());
        assertEquals("清洁", listVo.getPureVos().get(1).getName());
    }

    /**
     * 测试 properties 中找不到标识
     */
    @Test
    public void assertUnknownPropertyNotTranslated() {
        PureVo vo = new PureVo("unknown");
        VoTranslator.translateFields(vo,"zh_CN");
        assertEquals("unknown", vo.getName());
    }

    /**
     * 测试 List 中某个对象的属性值在 properties 中找不到
     */
    @Test
    public void assertUnknownPropertyInListNotTranslated() {
        listVo.setPureVos(Arrays.asList(
                new PureVo("food"),
                new PureVo("unknown"),
                new PureVo("clean")
        ));
        VoTranslator.translateFields(listVo,"zh_CN");
        assertEquals("食品", listVo.getPureVos().get(0).getName());
        assertEquals("unknown", listVo.getPureVos().get(1).getName());
        assertEquals("清洁", listVo.getPureVos().get(2).getName());
    }

    /**
     * 测试 List 中某个元素为 null
     */
    @Test
    public void assertPropertyTranslatedWhenNullInList() {
        listVo.setList(Arrays.asList("food", null, "clean"));
        VoTranslator.translateFields(listVo,"zh_CN");
        assertEquals("食品", listVo.getList().get(0));
        assertNotNull(listVo.getList().get(1));
        assertEquals("清洁", listVo.getList().get(2));
    }
}
