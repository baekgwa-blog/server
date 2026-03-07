package baekgwa.blogserver.infra.view.store;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import baekgwa.blogserver.infra.view.type.ViewDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PackageName : baekgwa.blogserver.infra.view.store
 * FileName    : ViewCountRedisStore
 * Author      : Baekgwa
 * Date        : 25. 11. 3.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 11. 3.     Baekgwa               Initial creation
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ViewCountRedisStore implements ViewCountStore {

	private static final DefaultRedisScript<Long> VIEW_COUNT_SCRIPT;

	static {
		VIEW_COUNT_SCRIPT = new DefaultRedisScript<>();
		VIEW_COUNT_SCRIPT.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/view_count_increment.lua")));
		VIEW_COUNT_SCRIPT.setResultType(Long.class);
	}

	private final StringRedisTemplate st;

	@Override
	public void incrementViewCount(ViewDomain domain, Long id, String remoteAddr) {
		String setKey = "viewed:today:" + domain.getDeduplicationKey() + ":" + id;
		String hashKey = domain.getKey();

		Long result = st.execute(
			VIEW_COUNT_SCRIPT,
			List.of(setKey, hashKey),
			remoteAddr,
			id.toString(),
			String.valueOf(getSecondsUntilMidnight())
		);

		if (result == null || result == 0L) {
			log.debug("remoteAddr : {}, postId : {} 는 오늘 이미 본 블로그 글 입니다. 조회수가 증가되지 않습니다.", remoteAddr, id);
		}
	}

	/**
	 * return view Count Map
	 * key : pk
	 * value : increment count
	 * @param domain ViewDomain
	 * @return map<pk, viewCount>
	 */
	@Override
	public Map<Long, Long> getAllViewCount(ViewDomain domain) {
		String hashKey = domain.getKey();
		Map<Object, Object> entries = st.opsForHash().entries(hashKey);
		return entries.entrySet().stream()
				.collect(Collectors.toMap(
						entry -> Long.parseLong((String)entry.getKey()),
						entry -> Long.parseLong((String)entry.getValue())
				));
	}

	@Override
	public void clearViewCount(ViewDomain domain) {
		st.delete(domain.getKey());
	}

	private long getSecondsUntilMidnight() {
		ZoneId kst = ZoneId.of("Asia/Seoul");
		ZonedDateTime now = ZonedDateTime.now(kst);
		ZonedDateTime nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay(kst);
		return Duration.between(now, nextMidnight).toSeconds();
	}
}
