/**
 *
 */
package au.org.ala.sds.model;

import au.org.ala.sds.dao.SensitivityCategoryXmlDao;
import au.org.ala.sds.util.Configuration;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import au.org.ala.sds.dao.SensitivityCategoryXmlDao;
import au.org.ala.sds.util.Configuration;
import org.apache.poi.util.StringUtil;

/**
 *
 * @author Peter Flemming (peter.flemming@csiro.au)
 */
public class SensitivityCategoryFactory {

    protected static final Logger logger = Logger.getLogger(SensitivityCategoryFactory.class);

    private static final String CATEGORIES_RESOURCE = "sensitivity-categories.xml";

    private static Map<String, SensitivityCategory> categories;

    public static SensitivityCategory getCategory(String key) {

        if(StringUtils.isEmpty(key)){
            return SensitivityCategory.DEFAULT_CATEGORY;
        }

        if (categories == null) {
            initCategories();
        }
        SensitivityCategory category = categories.get(key);
        if (category == null)
            logger.warn("No category for " + key);
        return category;
    }

    private static void initCategories() {
        URL url = null;
        InputStream is = null;

        try {
            url = new URL(Configuration.getInstance().getCategoryUrl());
            is = url.openStream();
        } catch (Exception e) {
            logger.warn("Exception occurred getting categories list from " + url, e);
            is = SensitivityCategoryFactory.class.getClassLoader().getResourceAsStream(CATEGORIES_RESOURCE);
            if (is == null) {
                logger.error("Unable to read " + CATEGORIES_RESOURCE + " from jar file");
            } else {
                logger.info("Reading bundled resource " + CATEGORIES_RESOURCE + " from jar file");
            }
        }

        SensitivityCategoryXmlDao dao = new SensitivityCategoryXmlDao(is);
        try {
            categories = dao.getMap();
        } catch (Exception e) {
            logger.error("Exception occurred parsing categories list from " + is, e);
        }
    }
}
