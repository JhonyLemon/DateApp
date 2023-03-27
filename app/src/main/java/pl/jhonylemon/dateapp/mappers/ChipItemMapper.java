package pl.jhonylemon.dateapp.mappers;

import pl.jhonylemon.dateapp.models.ChipItem;

import java.util.ArrayList;
import java.util.List;


public class ChipItemMapper {

    private ChipItemMapper(){}

    public static Integer mapChipItemToInteger(ChipItem source,List<String> texts){
        return texts.indexOf(source.getText());
    }

    public static List<Integer> mapListChipItemToListInteger(List<ChipItem> source,List<String> texts){
        List<Integer> indexes= new ArrayList<>();
        for (ChipItem item :
                source) {
            indexes.add(texts.indexOf(item.getText()));
        }
        return indexes;
    }

    public static ChipItem mapIntegerToChipItem(Integer source,List<String> texts){
        return new ChipItem(texts.get(source));
    }

    public static List<ChipItem> mapListIntegerToListChipItem(List<Long> source,List<String> texts){
        List<ChipItem> chipItems = new ArrayList<>();
        for(var index : source){
            chipItems.add(
                    new ChipItem(
                            texts.get(index.intValue()),
                            null,
                            index.intValue()
                    )
            );
        }
        return chipItems;
    }

}
