package io.quarkus.annotation.processor.generate_doc;

import java.util.List;

import io.quarkus.annotation.processor.Constants;

class SummaryTableDocFormatter implements DocFormatter {
    private static final String TABLE_CLOSING_TAG = "\n|===";
    private static final String TABLE_ROW_FORMAT = "\n\n|<<%s, %s>>\n\n%s|%s %s\n|%s\n| %s";
    private static final String TABLE_HEADER_FORMAT = "== Summary\n%s\n[.configuration-reference, cols=\"65,.^17,.^13,^.^5\"]\n|===\n|Configuration property|Type|Default|Lifecycle";

    /**
     * Generate configuration keys in table format.
     * Generated table will contain a key column that points to the long descriptive format.
     */
    @Override
    public String format(List<ConfigItem> configItems) {
        final String tableHeaders = String.format(TABLE_HEADER_FORMAT, Constants.CONFIG_PHASE_LEGEND);
        StringBuilder generatedAsciiDoc = new StringBuilder(tableHeaders);

        for (ConfigItem configItem : configItems) {
            String typeContent = "";
            if (configItem.hasAcceptedValues()) {
                typeContent = DocGeneratorUtil.joinAcceptedValues(configItem.getAcceptedValues());
            } else if (configItem.hasType()) {
                typeContent = configItem.computeTypeSimpleName();
                final String javaDocLink = configItem.getJavaDocSiteLink();
                if (!javaDocLink.isEmpty()) {
                    typeContent = String.format("link:%s[%s]\n", javaDocLink, typeContent);
                }
            }

            String doc = configItem.getConfigDoc();
            String firstLineDoc = "";
            if (doc != null && !doc.isEmpty()) {
                int firstDot = doc.indexOf('.');
                firstLineDoc = firstDot != -1 ? doc.substring(0, firstDot + 1) : doc.trim() + '.';
            }

            final String typeDetail = DocGeneratorUtil.getTypeFormatInformationNote(configItem);
            final String defaultValue = configItem.getDefaultValue();
            generatedAsciiDoc.append(String.format(TABLE_ROW_FORMAT,
                    getAnchor(configItem), configItem.getKey(),
                    firstLineDoc,
                    typeContent, typeDetail,
                    defaultValue.isEmpty() ? Constants.EMPTY : String.format("`%s`", defaultValue),
                    configItem.getConfigPhase().getIllustration()));
        }

        generatedAsciiDoc.append(TABLE_CLOSING_TAG); // close table
        return generatedAsciiDoc.toString();
    }

}
