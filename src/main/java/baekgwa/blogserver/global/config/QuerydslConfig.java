package baekgwa.blogserver.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

/**
 * PackageName : baekgwa.blogserver.global.config
 * FileName    : QuerydslConfig
 * Author      : Baekgwa
 * Date        : 2025-05-30
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-05-30     Baekgwa               Initial creation
 */
@Configuration
public class QuerydslConfig {
	@Bean
	public JPAQueryFactory jpaQueryFactory(final EntityManager em) {
		return new JPAQueryFactory(em);
	}
}
