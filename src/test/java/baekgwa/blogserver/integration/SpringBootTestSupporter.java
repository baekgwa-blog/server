package baekgwa.blogserver.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import baekgwa.blogserver.domain.authentication.service.AuthService;
import baekgwa.blogserver.domain.category.service.CategoryService;
import baekgwa.blogserver.integration.factory.CategoryDataFactory;
import baekgwa.blogserver.model.category.repository.CategoryRepository;
import jakarta.persistence.EntityManager;

/**
 * PackageName : baekgwa.blogserver.integration
 * FileName    : SpringBootTestSupporter
 * Author      : Baekgwa
 * Date        : 2025-06-09
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-09     Baekgwa               Initial creation
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class SpringBootTestSupporter {

	/**
	 * mock Mvc
	 */
	@Autowired
	public MockMvc mockMvc;

	/**
	 * Entity Data Factory
	 */
	@Autowired
	protected CategoryDataFactory categoryDataFactory;

	/**
	 * Common
	 */
	@Autowired
	protected EntityManager em;
	@Autowired
	protected ObjectMapper objectMapper;

	/**
	 * Repository
	 */
	@Autowired
	protected CategoryRepository categoryRepository;

	/**
	 * service
	 */
	@Autowired
	protected CategoryService categoryService;
	@Autowired
	protected AuthService authService;

	/**
	 * MockBean
	 */
}
