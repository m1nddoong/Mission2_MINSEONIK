package com.example.market.shop.repo;

import static com.example.market.shop.entity.QItem.item;

import com.example.market.shop.dto.ProductSearchParams;
import com.example.market.shop.entity.Item;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


// impl 을 붙여서 메서드르 구현한다.
@Slf4j
@RequiredArgsConstructor
public class ItemQuerydslRepoImpl implements ItemQuerydslRepo {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Item> searchDynamic(ProductSearchParams searchParams) {
        log.info(searchParams.toString());

        // Querydsl을 사용하여 동적으로 쿼리 생성
        return queryFactory.selectFrom(item)
                .where(
                        containsName(searchParams.getName()), // 이름 검색
                        priceBetween(searchParams.getPriceFloor(), searchParams.getPriceCeil()) // 가격 범위 검색
                )
                .fetch();
    }

    private Predicate containsName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return item.name.contains(name);
    }


    private BooleanExpression priceBetween(Integer floor, Integer ceil) {
        if (floor == null && ceil == null) return null;
        if (floor == null) return priceLoe(ceil);
        if (ceil == null) return priceGoe(floor);
        return item.price.between(floor, ceil);
    }

    private BooleanExpression priceLoe(Integer price) {
        return price != null ? item.price.loe(price) : null;
    }

    private BooleanExpression priceGoe(Integer price) {
        return price != null ? item.price.goe(price) : null;
    }
}
