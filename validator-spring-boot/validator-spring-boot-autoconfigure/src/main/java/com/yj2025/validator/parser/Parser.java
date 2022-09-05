package com.yj2025.validator.parser;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by serv on 2016/12/3.
 */
public class Parser {

    private SAXReader xmlReader = new SAXReader();

    public List<Form> parser(File xmlFile) throws Exception {
        Document document = xmlReader.read(xmlFile);
        return parser(document);
    }

    public List<Form> parser(String content) throws Exception {
        Document document = xmlReader.read(content);
        return parser(document);
    }

    public List<Form> parser(URL url) throws Exception {
        Document document = xmlReader.read(url);
        return parser(document);
    }

    public List<Form> parser(InputStream inputStream) throws Exception {
        Document document = xmlReader.read(inputStream);
        return parser(document);
    }

    public List<Form> parser(Reader reader) throws Exception {
        Document document = xmlReader.read(reader);
        return parser(document);
    }


    protected List<Form> parser(Document document) throws Exception {

        List<Form> forms = (List<Form>) document.selectNodes("/validation/formset/form")
                .stream().map(o -> {
                    DefaultElement element = (DefaultElement) o;
                    Form form = new Form();
                    form.setName(element.attributeValue("name"));
                    form.setDescription(element.attributeValue("description"));

                    List<Field> fields = (List<Field>) element.elements().stream().map(o1 -> {
                        DefaultElement fieldElement = (DefaultElement) o1;
                        Field field = new Field();
                        field.setName(fieldElement.attributeValue("name"));
                        List<ValidatorExec> execList = (List<ValidatorExec>) fieldElement.elements().stream().map(o2 -> {
                            DefaultElement execs = (DefaultElement) o2;
                            ValidatorExec exec = new ValidatorExec();
                            exec.setName(execs.attributeValue("name"));
                            exec.setMsg(execs.attributeValue("msg"));
                            exec.setVar(execs.attributeValue("var"));
                            return exec;
                        }).collect(Collectors.toList());
                        field.setValidators(execList);
                        return field;
                    }).collect(Collectors.toList());

                    form.setFields(fields);
                    return form;
                }).collect(Collectors.toList());

        return forms;
    }


}
