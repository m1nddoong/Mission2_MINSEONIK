package com.example.market.shop.repo;


import com.example.market.shop.dto.ProductSearchParams;
import com.example.market.shop.entity.Item;
import java.util.List;

public interface ItemQuerydslRepo {

    // 이 인터페이스는 구현하는 구현체는 아래의 기능을 갖춰야 될 것이다.
    List<Item> searchDynamic(ProductSearchParams searchParams);


}
