package apothicprogression;

import java.util.*;
import net.minecraft.network.chat.*;

/** Split explicit rows before renderers that otherwise draw a component on one line. */
public final class TooltipRows {
    private TooltipRows() {}
    public static List<FormattedText> expand(List<? extends FormattedText> lines) {
        var out=new ArrayList<FormattedText>();
        for (var line:lines) {
            if (!line.getString().contains("\n")) { out.add(line); continue; }
            var current=new MutableComponent[]{Component.empty()};
            line.visit((style,text)-> {
                String[] parts=text.split("\n",-1);
                for (int i=0;i<parts.length;i++) {
                    if(i>0) { out.add(current[0]); current[0]=Component.empty(); }
                    current[0].append(Component.literal(parts[i]).setStyle(style));
                }
                return Optional.empty();
            },Style.EMPTY);
            out.add(current[0]);
        }
        return out;
    }
    public static List<Component> components(List<Component> lines) {
        return expand(lines).stream().map(c->(Component)c).toList();
    }
}
