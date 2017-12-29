package com.rabbit.jpa.template.test;

import com.rabbit.spring.data.annotation.TemplateQuery;
import com.rabbit.spring.data.repository.GenericJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * .
 * <p/>
 *
 * @author <a href="mailto:stormning@163.com">stormning</a>
 * @version V1.0, 16/3/15.
 */
public interface SampleRepository extends GenericJpaRepository<Sample, Long> {

	@TemplateQuery
    Page<Sample> findByContent(String content, Pageable pageable);

	@TemplateQuery
	List<Sample> findByTemplateQueryObject(SampleQuery sampleQuery, Pageable pageable);

	@TemplateQuery
	Long countContent(String content);

	@TemplateQuery
	List<SampleDTO> findDtos();

	// #{name?:'and content like :name'}
	@Query(nativeQuery = true, value = "select * from t_sample where content like ?1")
	List<Sample> findDtos2(String name);
}
