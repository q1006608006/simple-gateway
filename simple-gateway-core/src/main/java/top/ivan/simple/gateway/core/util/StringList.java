package top.ivan.simple.gateway.core.util;

import java.util.ArrayList;
import java.util.Collection;

public class StringList extends ArrayList<String> {

    private static final String[] TYPE = new String[0];

    public StringList() {
    }

    public StringList(Collection<String> collection) {
        super(collection);
    }

    public String[] toStringArray() {
        return toArray(TYPE);
    }

    public String toString(CharSequence join) {
        return String.join(join, this);
    }
}