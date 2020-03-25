package com.cpiclife.precisionmarketing.precision;

import com.cpiclife.precisionMarketing.PrecisionApplication;
import com.cpiclife.precisionMarketing.dao.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes= PrecisionApplication.class)
class PrecisionApplicationTests {
    @Autowired
    private FieldsInterface fieldsInterface;
    @Autowired
    private EnumInterface enumInterface;
    @Autowired
    private MetaInterface metaInterface;
    @Autowired
    private ResultInterface resultInterface;
    @Autowired
    private TaskInterface taskInterface;
    @Test
    void contextLoads() {
        System.out.println(fieldsInterface.getMax(0));
        System.out.println(fieldsInterface.findByTaskIdAndTimes(0, 0));

        System.out.println(enumInterface.getAllEnum());
        System.out.println(enumInterface.findEnumByCode("123", 123l));

        System.out.println(metaInterface.getAllVariable());
        System.out.println(metaInterface.findVariableByCode("field"));

        System.out.println(resultInterface.findByTaskId(0l));
        System.out.println(resultInterface.findByTaskIdAndTimes(0l, 1l));
        System.out.println(resultInterface.getMaxByTaskId(1l));

        System.out.println(taskInterface.findByPrecisionId(10));
        System.out.println(taskInterface.findByPrecisionIdAndUserId(10, "123"));
        System.out.println(taskInterface.findByPrecisionIdAndUserId(10, "123"));
    }

}
