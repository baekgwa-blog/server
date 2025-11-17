package baekgwa.blogserver.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import baekgwa.blogserver.domain.ai.service.AiService;
import baekgwa.blogserver.domain.authentication.service.AuthService;
import baekgwa.blogserver.domain.category.service.CategoryService;
import baekgwa.blogserver.domain.post.service.PostService;
import baekgwa.blogserver.domain.stack.service.StackService;
import baekgwa.blogserver.domain.tag.service.TagService;
import baekgwa.blogserver.infra.embedding.service.EmbeddingFailureService;
import baekgwa.blogserver.infra.embedding.service.EmbeddingService;
import baekgwa.blogserver.infra.upload.FileUploader;
import baekgwa.blogserver.infra.view.scheduler.ViewCountBatchService;
import baekgwa.blogserver.infra.view.store.ViewCountStore;
import baekgwa.blogserver.integration.factory.CategoryDataFactory;
import baekgwa.blogserver.integration.factory.EmbeddingFailureDataFactory;
import baekgwa.blogserver.integration.factory.PostDataFactory;
import baekgwa.blogserver.integration.factory.StackDataFactory;
import baekgwa.blogserver.integration.factory.TagDataFactory;
import baekgwa.blogserver.model.category.repository.CategoryRepository;
import baekgwa.blogserver.model.embedding.repository.EmbeddingFailureRepository;
import baekgwa.blogserver.model.post.post.repository.PostRepository;
import baekgwa.blogserver.model.post.tag.repository.PostTagRepository;
import baekgwa.blogserver.model.stack.post.repository.StackPostRepository;
import baekgwa.blogserver.model.stack.stack.repository.StackRepository;
import baekgwa.blogserver.model.tag.repository.TagRepository;
import dev.langchain4j.model.chat.StreamingChatModel;
import jakarta.persistence.EntityManager;
import software.amazon.awssdk.services.s3.S3Client;

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
	@Autowired
	protected TagDataFactory tagDataFactory;
	@Autowired
	protected PostDataFactory postDataFactory;
	@Autowired
	protected StackDataFactory stackDataFactory;
	@Autowired
	protected EmbeddingFailureDataFactory embeddingFailureDataFactory;

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
	@Autowired
	protected TagRepository tagRepository;
	@Autowired
	protected PostRepository postRepository;
	@Autowired
	protected PostTagRepository postTagRepository;
	@Autowired
	protected StackRepository stackRepository;
	@Autowired
	protected StackPostRepository stackPostRepository;
	@Autowired
	protected EmbeddingFailureRepository embeddingFailureRepository;

	/**
	 * service
	 */
	@Autowired
	protected CategoryService categoryService;
	@Autowired
	protected AuthService authService;
	@Autowired
	protected TagService tagService;
	@Autowired
	protected PostService postService;
	@Autowired
	protected StackService stackService;
	@Autowired
	protected AiService aiService;
	@Autowired
	protected ViewCountBatchService viewCountBatchService;
	@Autowired
	protected EmbeddingFailureService embeddingFailureService;

	/**
	 * MockBean
	 */
	@MockitoBean
	private S3Client s3Client;
	@MockitoBean
	protected FileUploader fileUploader;
	@MockitoBean
	protected StreamingChatModel streamingChatModel;
	@MockitoBean
	protected EmbeddingService embeddingService;
	@MockitoBean
	protected ViewCountStore viewCountStore;

	/**
	 * Static variable
	 */
	protected static final String REMOTE_ADDR = "0:0:0:0:0:0:0:1";
}
