module reseau.electrique {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;

    opens vue to javafx.graphics;

    exports vue;
    exports model;
    exports exceptions;
    exports fileOperation;
}
