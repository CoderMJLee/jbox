package io.github.codermjlee.pojo;

/**
 * @author MJ
 */
public interface IdObject {
    default Object getId() {
        return null;
    }
}
