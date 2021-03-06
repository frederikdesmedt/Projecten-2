package domain;

import java.util.Arrays;
import java.util.List;

/**
 * A {@link domain.PropertyListConverter} implementation that allows a String
 * (of themes) to be converted to a List of separate themes.
 *
 * @author Frederik De Smedt
 */
public class ThemeConverter implements PropertyListConverter<String, String> {

    @Override
    public List<String> toList(String prop) {
	return Arrays.asList(prop.split("\\s*,\\s*"));
    }

    @Override
    public String toProperty(List<String> list) {
	String separator = ", ";
	StringBuilder builder = new StringBuilder();
	list.forEach(s -> builder.append(s).append(separator));
	return builder.length() > 2 ? builder.substring(0, builder.length() - 2) : builder.toString();
    }

}
