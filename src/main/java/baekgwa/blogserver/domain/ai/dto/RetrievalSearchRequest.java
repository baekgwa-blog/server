package baekgwa.blogserver.domain.ai.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.domain.ai.dto
 * FileName    : RetrievalSearchRequest
 * Author      : Baekgwa
 * Date        : 25. 11. 11.
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 11.     Baekgwa               Initial creation
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RetrievalSearchRequest {
	@NotBlank
	private String sentence;
	private Integer topK;
	private List<String> filter;
}
