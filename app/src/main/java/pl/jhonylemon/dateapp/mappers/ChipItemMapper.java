package pl.jhonylemon.dateapp.mappers;

import pl.jhonylemon.dateapp.R;
import pl.jhonylemon.dateapp.models.ChipItem;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mapper()
public interface ChipItemMapper {

    @Named("ChipItemToInteger")
    default Integer mapChipItemToInteger(ChipItem source,List<String> texts){
        return texts.indexOf(source.getText());
    }

    @Named("ChipItemListToIntegerList")
    default List<Integer> mapListChipItemToListInteger(List<ChipItem> source,List<String> texts){
        List<Integer> indexes= new ArrayList<>();
        for (ChipItem item :
                source) {
            indexes.add(texts.indexOf(item.getText()));
        }
        return indexes;
    }

    @Named("IntegerToChipItem")
    default ChipItem mapIntegerToChipItem(Integer source,List<String> texts){
        return new ChipItem(texts.get(source));
    }

    @Named("IntegerListToChipItemList")
    default List<ChipItem> mapListIntegerToListChipItem(List<Integer> source,List<String> texts){
        List<ChipItem> chipItems = new ArrayList<>();
        for (Integer index :
                source) {
            chipItems.add(new ChipItem(texts.get(index)));
        }
        return chipItems;
    }

}
