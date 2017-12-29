package com.rabbit.jpa.template.test;

import org.junit.Assert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * .
 * <p/>
 *
 * @author <a href="mailto:stormning@163.com">stormning</a>
 * @version V1.0, 16/3/15.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = AppConfig.class)
public class JpaTest {
    //   @Autowired
    private SampleRepository sampleRepository;

    //	@Before
    public void addSomeSample() {
        sampleRepository.deleteAll();
        for (int i = 0; i < 10; i++) {
            Sample sample = new Sample();
            sample.setContent("hello world" + i);
            sampleRepository.save(sample);
        }
    }

    //	@Test
    public void findByTemplateQuery() {
        Page<Sample> samples = sampleRepository.findByContent("world", new PageRequest(1, 100));
        Assert.assertTrue(samples.getTotalElements() == 10);
    }

    //	@Test
    public void countByTemplateQuery() {
        long count = sampleRepository.countContent("world");
        Assert.assertTrue(count == 10);
    }

    //	@Test
    public void findByTemplateQueryAndReturnDTOs() {
        List<SampleDTO> dtos = sampleRepository.findDtos();
        Assert.assertTrue(dtos.size() == 10);
    }

    //	@Test
    public void findByTemplateQueryWithTemplateQueryObject() {
        SampleQuery sq = new SampleQuery();
        sq.setContent("world");
        List<Sample> samples = sampleRepository.findByTemplateQueryObject(sq, null);
        Assert.assertTrue(samples.size() == 10);
    }

    //	@Test
    public void findBySpringElQuery() {
        List<Sample> dtos = sampleRepository.findDtos2("%world%");
        Assert.assertTrue(dtos.size() == 10);
    }

}
