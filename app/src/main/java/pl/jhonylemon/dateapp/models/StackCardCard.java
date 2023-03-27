package pl.jhonylemon.dateapp.models;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class StackCardCard implements Serializable {
   private final String uuid;
   private final String name;
   private final String description;
   private final String url;
}
