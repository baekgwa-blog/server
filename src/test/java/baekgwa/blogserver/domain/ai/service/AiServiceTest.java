package baekgwa.blogserver.domain.ai.service;

import baekgwa.blogserver.integration.SpringBootTestSupporter;

/**
 * PackageName : baekgwa.blogserver.domain.ai.service
 * FileName    : AiServiceTest
 * Author      : Baekgwa
 * Date        : 25. 11. 17.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 17.     Baekgwa               Initial creation
 * 2026-03-15     Baekgwa               수동 임베딩 기능 제거로 인한 테스트 정리 (embeddingPosts → Data Pipeline 이관)
 */
class AiServiceTest extends SpringBootTestSupporter {
	// 수동 임베딩(embeddingPosts) 기능이 Data Pipeline 으로 이관되어 해당 테스트가 제거되었습니다.
	// searchPosts 는 외부 의존성(OpenAI, ElasticSearch) 이 있어 통합 테스트 환경에서 제외됩니다.
}
