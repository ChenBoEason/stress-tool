package com.eason.stress.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.namespace.QName;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;

/**
 * @author Eason(bo.chenb)
 * @description
 * @date 2019-12-13
 **/
public class JaxbUtils {

    private static final Logger logger = LoggerFactory.getLogger(JaxbUtils.class);

    /**
     * 多线程安全的Context
     */
    private JAXBContext jaxbContext;

    /**
     * @param types 所有需要序列化的Root对象的类型.
     */
    public JaxbUtils(Class<?>... types) {
        try {
            jaxbContext = JAXBContext.newInstance(types);
        } catch (JAXBException e) {
            logger.error("JAXBException types:{}", types, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Java Object->Xml.
     *
     * @param root
     * @return
     */
    public String toXml(Object root) {
        try {
            StringWriter writer = new StringWriter();
            createMarshaller("utf-8").marshal(root, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Java Object->Xml.
     */
    public String toXml(Object root, String encoding) {
        try {
            StringWriter writer = new StringWriter();
            createMarshaller(encoding).marshal(root, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * object 转为 xml ; 支持自定义名称；
     *
     * @param root
     * @param rootName
     * @param encoding
     * @param <T>
     * @return
     */
    public <T> String toXml(T root, String rootName, String encoding) {
        try {
            StringWriter writer = new StringWriter();
            JAXBElement<T> wrapperElement = new JAXBElement(new QName(rootName), root.getClass(), root);

            createMarshaller(encoding).marshal(wrapperElement, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Java Object->Xml, 特别支持对Root Element是Collection的情形.
     */
    @SuppressWarnings("unchecked")
    public String toXml(Collection root, String rootName, String encoding) {
        try {
            CollectionWrapper wrapper = new CollectionWrapper();
            wrapper.collection = root;

            JAXBElement<CollectionWrapper> wrapperElement = new JAXBElement<CollectionWrapper>(
                    new QName(rootName), CollectionWrapper.class, wrapper);

            StringWriter writer = new StringWriter();
            createMarshaller(encoding).marshal(wrapperElement, writer);

            return writer.toString();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Xml->Java Object.
     */
    @SuppressWarnings("unchecked")
    public <T> T fromXml(String xml) throws Exception {
        StringReader reader = new StringReader(xml);
        return (T) createUnmarshaller().unmarshal(reader);
    }

    /**
     * Xml->Java Object, 支持大小写敏感或不敏感.
     */
    @SuppressWarnings("unchecked")
    public <T> T fromXml(String xml, boolean caseSensitive) throws Exception {
        try {
            String fromXml = xml;
            if (!caseSensitive) {
                fromXml = xml.toLowerCase();
            }
            StringReader reader = new StringReader(fromXml);
            return (T) createUnmarshaller().unmarshal(reader);
        } catch (JAXBException e) {
            throw new Exception(e);
        }
    }

    /**
     * 创建Marshaller, 设定encoding(可为Null).
     */
    private Marshaller createMarshaller(String encoding) {
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            //默认是 utf-8
            marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding == null ? "UTF-8" : encoding);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            return marshaller;
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建UnMarshaller.
     */
    private Unmarshaller createUnmarshaller() {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return unmarshaller;
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 封装Root Element 是 Collection的情况.
     */
    public static class CollectionWrapper {
        @SuppressWarnings("unchecked")
        @XmlAnyElement
        protected Collection collection;
    }

}
