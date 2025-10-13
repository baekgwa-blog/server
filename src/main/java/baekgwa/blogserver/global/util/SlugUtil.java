package baekgwa.blogserver.global.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * PackageName : baekgwa.blogserver.global.util
 * FileName    : SlugUtil
 * Author      : Baekgwa
 * Date        : 2025-06-28
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-06-28     Baekgwa               Initial creation
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SlugUtil {
	public static String generateSlug(@NonNull String title) {
		// 1. 소문자로 변환
		String lower = title.toLowerCase();

		// 2. 공백, 언더스코어, 슬래시, 점을 하이픈으로 변환
		String hyphenated = lower.replaceAll("[\\s/_\\.]+", "-");

		// 3. 한글, 영문 소문자, 숫자, 하이픈만 남기고 나머지 제거 (이모지, 특수문자 제거)
		String cleaned = hyphenated.replaceAll("[^a-z0-9ㄱ-ㅎㅏ-ㅣ가-힣-]", "");

		// 4. 연속된 하이픈을 하나로 합치기
		String singleHyphens = cleaned.replaceAll("-{2,}", "-");

		// 5. 앞뒤 하이픈 제거
		return singleHyphens.replaceAll("(^-+)|(-+$)", "");
	}
}
