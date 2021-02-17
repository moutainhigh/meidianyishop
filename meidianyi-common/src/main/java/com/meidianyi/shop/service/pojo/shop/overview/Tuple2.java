package com.meidianyi.shop.service.pojo.shop.overview;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * @Author:liufei
 * @Date:2019/7/19
 * @Description:
 */
public class Tuple2<E1,E2> implements Iterable<Object>, Serializable {
	private static final long serialVersionUID = 4633679195259490261L;
	
	private E1 e1;
    private E2 e2;

    public E1 getE1() {
        return e1;
    }

    public void setE1(E1 e1) {
        this.e1 = e1;
    }

    public E2 getE2() {
        return e2;
    }

    public void setE2(E2 e2) {
        this.e2 = e2;
    }

    public Tuple2(E1 e1, E2 e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tuple2)){
            return false;
        }
        Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;
        return Objects.equals(getE1(), tuple2.getE1()) &&
                Objects.equals(getE2(), tuple2.getE2());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getE1(), getE2());
    }

    @Override
    public Iterator<Object> iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer<? super Object> action) {

    }

    @Override
    public Spliterator<Object> spliterator() {
        return null;
    }
}
