package cn.gniic.common.filter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.util.ContentCachingRequestWrapper;

/**
 * @author FAYUAN.PENG
 * @version \$Id: ModifyHttpServletRequestWrapper.java,  2020-06-13 11:53 FAYUAN.PENG Exp $$
 */
public class ModifyHttpServletRequestWrapper extends ContentCachingRequestWrapper {

    private List<String> headerNames = Lists.newArrayList();
    private Map<String, String> additionalHeaders = Maps.newHashMap();

    public ModifyHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        Enumeration<String> enumeration = super.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            headerNames.add(name);
        }
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(headerNames);
    }

    public void putHeader(String name, String value) {
        additionalHeaders.put(name, value);
        headerNames.add(name);
    }

    @Override
    public String getHeader(String name) {
        if (additionalHeaders.containsKey(name)) {
            return additionalHeaders.get(name);
        } else {
            return super.getHeader(name);
        }
    }
}
