/**
 * Created on  13-10-02 18:38
 */
package com.alicp.jetcache.anno.method;

import com.alicp.jetcache.anno.CacheConsts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
class ExpressionUtil {

    private static final Logger logger = LoggerFactory.getLogger(ExpressionUtil.class);

    public static boolean evalCondition(CacheInvokeContext context) {
        CacheInvokeConfig cic = context.getCacheInvokeConfig();
        String condition = cic.getCachedAnnoConfig().getCondition();
        try {
            if (cic.getConditionEvaluator() == null) {
                if (CacheConsts.UNDEFINED_STRING.equals(condition)) {
                    cic.setConditionEvaluator(o -> true);
                } else {
                    ExpressionEvaluator e = new ExpressionEvaluator(condition);
                    cic.setConditionEvaluator((o) -> (Boolean) e.apply(o));
                }
            }
            return cic.getConditionEvaluator().apply(context);
        } catch (Exception e) {
            logger.error("error occurs when eval condition \"" + condition + "\" in " + context.getMethod() + "." + e.getClass() + ":" + e.getMessage());
            return false;
        }
    }

    public static boolean evalUnless(CacheInvokeContext context) {
        CacheInvokeConfig cic = context.getCacheInvokeConfig();
        String unless = cic.getCachedAnnoConfig().getUnless();
        try {
            if (cic.getUnlessEvaluator() == null) {
                if (CacheConsts.UNDEFINED_STRING.equals(unless)) {
                    cic.setUnlessEvaluator(o -> false);
                } else {
                    ExpressionEvaluator e = new ExpressionEvaluator(unless);
                    cic.setUnlessEvaluator((o) -> (Boolean) e.apply(o));
                }
            }
            return cic.getUnlessEvaluator().apply(context);
        } catch (Exception e) {
            logger.error("error occurs when eval unless \"" + unless + "\" in " + context.getMethod() + "." + e.getClass() + ":" + e.getMessage());
            return true;
        }
    }

    public static Object evalKey(CacheInvokeContext context) {
        CacheInvokeConfig cic = context.getCacheInvokeConfig();
        String keyScript = cic.getCachedAnnoConfig().getKey();
        try {
            if (cic.getKeyEvaluator() == null) {
                if (CacheConsts.UNDEFINED_STRING.equals(keyScript)) {
                    cic.setKeyEvaluator(o -> {
                        CacheInvokeContext c = (CacheInvokeContext) o;
                        return c.getArgs() == null ? "_$JETCACHE_NULL_KEY$_" : c.getArgs();
                    });
                } else {
                    ExpressionEvaluator e = new ExpressionEvaluator(keyScript);
                    cic.setKeyEvaluator((o) -> e.apply(o));
                }
            }
            return cic.getKeyEvaluator().apply(context);
        } catch (Exception e) {
            logger.error("error occurs when eval key \"" + keyScript + "\" in " + context.getMethod() + "." + e.getClass() + ":" + e.getMessage());
            return null;
        }
    }

}
