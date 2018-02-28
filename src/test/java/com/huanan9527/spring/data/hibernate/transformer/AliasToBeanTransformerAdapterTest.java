package com.huanan9527.spring.data.hibernate.transformer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class AliasToBeanTransformerAdapterTest {


    @Test
    public void test() throws NoSuchFieldException {
        AliasToBeanTransformerAdapter adapter = new AliasToBeanTransformerAdapter(User.class);
        Long startTime = System.currentTimeMillis();
//        List<String> aliases = Arrays.asList("name", "age", "person_like", "attrs.password", "attrs.role", "address.name", "address.zip_code", "set.name", "set.zipCode", "list.name", "list.zipCode", "setmap.name", "setmap.code");
//        List<Object> tuple = Arrays.asList("SilentWu", 23, "wefwgweg", "123456", "admin", "成都市", "323523554", "绵阳市", "4444444", "绵阳市2", "432425", "weggggg", "3243234");
        List<String> aliases = Arrays.asList("name", "age", "person_like", "attrs.password", "attrs.role", "address.name", "address.zip_code");
        List<Object> tuple = Arrays.asList("SilentWu", 23, "4444444", "123456", "admin", "成都市", "323523554");

        Object o = adapter.transformTuple(tuple.toArray(), (String[]) aliases.toArray());
        System.out.println(JSON.toJSONString(o, SerializerFeature.DisableCircularReferenceDetect));

        Long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);

//        Set<TestDto> sets1 = new HashSet<>();
//        Set<TestDto> sets2 = new HashSet<>();
//
//        sets1.add(new TestDto("test1"));
//        sets2.add(new TestDto("test1"));
//
//        sets1.addAll(sets2);
//
//        System.out.println(JSON.toJSONString(sets1));
    }


    class TestDto {
        private String name;

        public TestDto(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestDto testDto = (TestDto) o;

            return name != null ? name.equals(testDto.name) : testDto.name == null;
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }
    }

}