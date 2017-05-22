package com.ngcomp.analytics.engine.filter;


import com.google.common.base.Strings;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * TumblerUser: rparashar
 * Date: 7/20/13
 * Time: 11:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class RequestFilter implements Filter
{

    final static Logger logger = Logger.getLogger(RequestFilter.class);
    static String eol;
    public void destroy() {}

    public void init(FilterConfig fConfig) throws ServletException
    {
        eol = System.getProperty("line.separator");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        logger.info("---------------------------");
        logger.info(httpRequest.getRequestURI());

        Map<String, String[]> params = httpRequest.getParameterMap();
        StringBuilder stbr = new StringBuilder();
        for(String p : params.keySet())
        {
            stbr.append(p).append("=>");
            for(String s : params.get(p))
            {
                stbr.append(String.valueOf(s)).append(" ");
            }
        }
        if(Strings.isNullOrEmpty(stbr.toString())){
            logger.info(stbr.toString());
            System.out.println(stbr.toString());
        }
        chain.doFilter(request, response);
    }

}
