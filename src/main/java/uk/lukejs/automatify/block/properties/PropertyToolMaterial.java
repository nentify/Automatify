package uk.lukejs.automatify.block.properties;

import com.google.common.base.CharMatcher;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.item.Item;

import java.util.Collection;
import java.util.Map;

public class PropertyToolMaterial extends PropertyHelper<Item.ToolMaterial> {

    private final ImmutableSet<Item.ToolMaterial> allowedValues;
    private final Map<String, Item.ToolMaterial> nameToMaterial = Maps.newHashMap();

    protected PropertyToolMaterial(String name, Collection<Item.ToolMaterial> allowedValues) {
        super(name, Item.ToolMaterial.class);

        this.allowedValues = ImmutableSet.copyOf(allowedValues);

        for (Item.ToolMaterial toolMaterial : allowedValues) {
            String toolMaterialName = getName(toolMaterial);

            if (nameToMaterial.containsKey(toolMaterialName))
            {
                throw new IllegalArgumentException("Multiple values have the same name '" + toolMaterialName + "'");
            }

            nameToMaterial.put(toolMaterialName, toolMaterial);
        }
    }

    @Override
    public Collection<Item.ToolMaterial> getAllowedValues() {
        return allowedValues;
    }

    @Override
    public Optional<Item.ToolMaterial> parseValue(String value) {
        return Optional.fromNullable(nameToMaterial.get(value));
    }

    @Override
    public String getName(Item.ToolMaterial value) {
        CharMatcher matcher = CharMatcher.inRange('a', 'z')
                .or(CharMatcher.is('_'));

        return matcher.retainFrom(value.name().toLowerCase());
    }

    public static PropertyToolMaterial create(String name)
    {
        return create(name, Predicates.alwaysTrue());
    }

    public static PropertyToolMaterial create(String name, Predicate<Item.ToolMaterial> filter)
    {
        return create(name, Collections2.filter(Lists.newArrayList(Item.ToolMaterial.values()), filter));
    }

    public static PropertyToolMaterial create(String name, Collection<Item.ToolMaterial> values)
    {
        return new PropertyToolMaterial(name, values);
    }
}
