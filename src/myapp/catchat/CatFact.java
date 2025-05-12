package myapp.catchat;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

// Класс для хранения занимательного котофакта
@Data // Генерирует геттеры, сеттеры, toString, equals и hashCode
@NoArgsConstructor // Генерирует конструктор без аргументов
@AllArgsConstructor // Генерирует конструктор со всеми аргументами

public class CatFact implements Serializable {
    private String fact;
    private int length;
}
