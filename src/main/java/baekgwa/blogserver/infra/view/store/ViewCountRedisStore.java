package baekgwa.blogserver.infra.view.store;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import baekgwa.blogserver.infra.view.type.ViewDomain;
import lombok.RequiredArgsConstructor;

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
@Component
@RequiredArgsConstructor
public class ViewCountRedisStore implements ViewCountStore {

	private final StringRedisTemplate st;

	@Override
	public void incrementViewCount(ViewDomain domain, Long id) {
		String hashKey = domain.getKey();
		String field = id.toString();
		st.opsForHash().increment(hashKey, field, 1L);
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
}
