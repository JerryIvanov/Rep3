package LineAndStation;

public class LinesAndStations {
    private static final String [] str = {
            "Красная.ветка (1)\n",
            "1.  Бульвар Рокоссовского\n" ,
            "2.  Черкизовская\n" ,
            "3.  Преображенская площадь\n" ,
            "4.  Сокольники\n" ,
            "5.  Красносельская\n" ,
            "6.  Комсомольская\n" ,
            "7.  Красные ворота\n" ,
            "8.  Чистые пруды\n" ,
            "9.  Лубянка\n" ,
            "10. Охотный ряд\n" ,
            "11. Библиотека имени Ленина\n" ,
            "12. Кропоткинская\n" ,
            
            "13. Парк Культуры\n" ,
            
            "14. Фрунзенская\n" ,
            
            "15. Спортивная\n" ,
            
            "16. Воробьевы горы\n" ,
            
            "17. Университет\n" ,
            
            "18. Проспект Вернадского\n" ,
            
            "19. Юго-Западная\n" ,
            
            "20. Тропарёво\n" ,
            
            "21. Румянцево\n" ,
            
            "22. Саларьево\n" ,
            
            "23. Филатов Луг\n" ,
            
            "24. Прокшино\n" ,
            
            "25. Ольховая\n" ,
            
            "26. Коммунарка\n" ,
            
            "Зеленая.ветка (2)\n" ,
            
            "1.  Ховрино\n" ,
            
            "2.  Беломорская\n" ,
            
            "3.  Речной вокзал\n" ,
            
            "4.  Водный стадион\n" ,
            
            "5.  Войковская\n" ,
            
            "6.  Сокол\n" ,
            
            "7.  Аэропорт\n" ,
            
            "8.  Динамо\n" ,
            
            "9.  Белорусская\n" ,
            
            "10. Маяковская\n" ,
            
            "11. Тверская\n" ,
            
            "12. Театральная\n" ,
            
            "13. Новокузнецкая\n" ,
            
            "14. Павелецкая\n" ,
            
            "15. Автозаводская\n" ,
            
            "16. Технопарк\n" ,
            
            "17. Коломенская\n" ,
            
            "18. Каширская\n" ,
            
            "19. Кантемировская\n" ,
            
            "20. Царицыно\n" ,
            
            "21. Орехово\n" ,
            
            "22. Домодедовская\n" ,
            
            "23. Красногвардейская\n" ,
            
            "24. Алма-Атинская\n" ,
            
            
            "Синяя.ветка (3)\n" ,
            "1.  Пятницкое шоссе\n" ,
            
            "2.  Митино\n" ,
            
            "3.  Волоколамская\n" ,
            
            "4.  Мякинино\n" ,
            
            "5.  Строгино\n" ,
            
            "6.  Крылатское\n" ,
            
            "7.  Молодежная\n" ,
            
            "8.  Кунцевская\n" ,
            
            "9.  Славянский бульвар\n" ,
            
            "10. Парк Победы\n" ,
            
            "11. Киевская\n" ,
            
            "12. Смоленская\n" ,
            
            "13. Арбатская\n" ,
            
            "14. Площадь Революции\n" ,
            
            "15. Курская\n" ,
            
            "16. Бауманская\n" ,
            
            "17. Электрозаводская\n" ,
            
            "18. Семеновская\n" ,
            
            "19. Партизанская\n" ,
            
            "20. Измайловская\n" ,
            
            "21. Первомайская\n" ,
            
            "22. Щелковская\n" ,
            
            "Голубая.ветка (4)\n" ,
            "1.  Александровский сад\n" ,
            
            "2.  Арбатская\n" ,
            
            "3.  Смоленская\n" ,
            
            "4.  Киевская\n" ,
            
            "5.  Студенческая\n" ,
            
            "6.  Кутузовская\n" ,
            
            "7.  Фили\n" ,
            
            "8.  Багратионовская\n" ,
            
            "9.  Филевский парк\n" ,
            
            "10. Пионерская\n" ,
            
            "11. Кунцевская\n" ,
            
            "12. Выставочная\n" ,
            
            "13. Международная\n" ,
            
            "Коричневая.ветка (5)\n" ,
            "1.  Киевская\n" ,
            
            "2.  Парк Культуры\n" ,
            
            "3.  Октябрьская\n" ,
            
            "4.  Добрынинская\n" ,
            
            "5.  Павелецкая\n" ,
            
            "6.  Таганская\n" ,
            
            "7.  Курская\n" ,
            
            "8.  Комсомольская\n" ,
            
            "9.  Проспект Мира\n" ,
            
            "10. Новослободская\n" ,
            
            "11. Белорусская\n" ,
            
            "12. Краснопресненская\n" ,
            
            "Оранжевая.ветка (6)\n" ,
            
            "1.  Новоясеневская\n" ,
            
            "2.  Ясенево\n" ,
            
            "3.  Теплый стан\n" ,
            
            "4.  Коньково\n" ,
            
            "5.  Беляево\n" ,
            
            "6.  Калужская\n" ,
            
            "7.  Новые Черёмушки\n" ,
            
            "8.  Профсоюзная\n" ,
            
            "9.  Академическая\n" ,
            
            "10. Ленинский проспект\n" ,
            
            "11. Шаболовская\n" ,
            
            "12. Октябрьская\n" ,
            
            "13. Третьяковская\n" ,
            
            "14. Китай-город\n" ,
            
            "15. Тургеневская\n" ,
            
            "16. Сухаревская\n" ,
            
            "17. Проспект Мира\n" ,
            
            "18. Рижская\n" ,
            
            "19. Алексеевская\n" ,
            
            "20. ВДНХ\n" ,
            
            "21. Ботанический сад\n" ,
            
            "22. Свиблово\n" ,
            
            "23. Бабушкинская\n" ,
            
            "24. Медведково\n" ,
            
            "Фиолетовая.ветка (7)\n" ,
            
            "1.  Котельники\n" ,
            
            "2.  Жулебино\n" ,
            
            "3.  Лермонтовский проспект\n" ,
            
            "4.  Выхино\n" ,
            
            "5.  Рязанский проспект\n" ,
            
            "6.  Кузьминки\n" ,
            
            "7.  Текстильщики\n" ,
            
            "8.  Волгоградский проспект\n" ,
            
            "9.  Пролетарская\n" ,
            
            "10. Таганская\n" ,
            
            "11. Китай-город\n" ,
            
            "12. Кузнецкий мост\n" ,
            
            "13. Пушкинская\n" ,
            
            "14. Баррикадная\n" ,
            
            "15. Улица 1905 года\n" ,
            
            "16. Беговая\n" ,
            
            "17. Полежаевская\n" ,
            
            "18. Октябрьское Поле\n" ,
            
            "19. Щукинская\n" ,
            
            "20. Спартак\n" ,
            
            "21. Тушинская\n" ,
            
            "22. Сходненская\n" ,
            
            "23. Планерная\n" ,
            
            "Желтая.ветка (8)\n" ,
            
            "1.  Новокосино\n" ,
            
            "2.  Новогиреево\n" ,
            
            "3.  Перово\n" ,
            
            "4.  Шоссе Энтузиастов\n" ,
            
            "5.  Авиамоторная\n" ,
            
            "6.  Площадь Ильича\n" ,
            
            "7.  Марксистская\n" ,
            
            "8.  Третьяковская\n" ,
            
            "9.  Деловой центр\n" ,
            
            "10. Парк Победы\n" ,
            
            "11. Минская\n" ,
            
            "12. Ломоносовский проспект\n" ,
            
            "13. Раменки\n" ,
            
            "14. Мичуринский проспект\n" ,
            
            "15. Озёрная\n" ,
            
            "16. Говорово\n" ,
            
            "17. Солнцево\n" ,
            
            "18. Боровское шоссе\n" ,
            
            "19. Новопеределкино\n" ,
            
            "20. Рассказовка\n" ,
            
            "Серая.ветка (9)\n" ,
            
            "1.  Бульвар Дмитрия Донского\n" ,
            
            "2.  Аннино\n" ,
            
            "3.  Улица академика Янгеля\n" ,
            
            "4.  Пражская\n" ,
            
            "5.  Южная\n" ,
            
            "6.  Чертановская\n" ,
            
            "7.  Севастопольская\n" ,
            
            "8.  Нахимовский Проспект\n" ,
            
            "9.  Нагорная\n" ,
            
            "10. Нагатинская\n" ,
            
            "11. Тульская\n" ,
            
            "12. Серпуховская\n" ,
            
            "13. Полянка\n" ,
            
            "14. Боровицкая\n" ,
            
            "15. Чеховская\n" ,
            
            "16. Цветной бульвар\n" ,
            
            "17. Менделеевская\n" ,
            
            "18. Савеловская\n" ,
            
            "19. Дмитровская\n" ,
            
            "20. Тимирязевская\n" ,
            
            "21. Петровско-Разумовская\n" ,
            
            "22. Владыкино\n" ,
            
            "23. Отрадное\n" ,
            
            "24. Бибирево\n" ,
            
            "25. Алтуфьево\n" ,
            
            "Салатовая.ветка (10) \n" ,
            "1.  Зябликово\n" ,
            
            "2.  Шипиловская\n" ,
            
            "3.  Борисово\n" ,
            
            "4.  Марьино\n" ,
            
            "5.  Братиславская\n" ,
            
            "6.  Люблино\n" ,
            
            "7.  Волжская\n" ,
            
            "8.  Печатники\n" ,
            
            "9.  Кожуховская\n" ,
            
            "10. Дубровка\n" ,
            
            "11. Крестьянская застава\n" ,
            
            "12. Римская\n" ,
            
            "13. Чкаловская\n" ,
            
            "14. Сретенский бульвар\n" ,
            
            "15. Трубная\n" ,
            
            "16. Достоевская\n" ,
            
            "17. Марьина роща\n" ,
            
            "18. Бутырская\n" ,
            
            "19. Фонвизинская\n" ,
            
            "20. Петровско-Разумовская\n" ,
            
            "21. Окружная\n" ,
            
            "22. Верхние Лихоборы\n" ,
            
            "23. Селигерская\n" ,
            
            "БКЛ.ветка (11)\n" ,
            
            "1.  Савёловская\n" ,
            
            "2.  Петровский парк\n" ,
            
            "3.  ЦСКА\n" ,
            
            "4.  Хорошёвская\n" ,
            
            "5.  Шелепиха\n" ,
            
            "6.  Деловой центр\n" ,
            
            "Бутовская.ветка (12)\n" ,
            
            "1.  Битцевский парк\n" ,
            
            "2.  Лесопарковая\n" ,
            
            "3.  Улица Старокачаловская\n" ,
            
            "4.  Улица Скобелевская\n" ,
            
            "5.  Бульвар адмирала Ушакова\n" ,
            
            "6.  Улица Горчакова\n" ,
            
            "7.  Бунинская Аллея\n" ,
            
            "МЦД.ветка (14)\n" ,
            "1.  Окружная\n" ,
            
            "2.  Владыкино\n" ,
            
            "3.  Ботанический Сад\n" ,
            
            "4.  Ростокино\n" ,
            
            "5.  Белокаменная\n" ,
            
            "6.  Бульвар Рокоссовского\n" ,
            
            "7.  Локомотив\n" ,
            
            "8.  Измайлово\n" ,
            
            "9.  Соколиная Гора\n" ,
            
            "10. Шоссе Энтузиастов\n" ,
            
            "11. Андроновка\n" ,
            
            "12. Нижегородская\n" ,
            
            "13. Новохохловская\n" ,
            "14. Угрешская\n" ,
            "15. Дубровка\n" ,
            "16. Автозаводская\n" ,
            "17. ЗИЛ\n" ,
            "18. Верхние Котлы\n" ,
            "19. Крымская\n" ,
            "20. Площадь Гагарина\n" ,
            "21. Лужники\n" ,
            "22. Кутузовская\n" ,
            "23. Деловой центр\n" ,
            "24. Шелепиха\n" ,
            "25. Хорошёво\n" ,
            "26. Зорге\n" ,
            "27. Панфиловская\n" ,
            "28. Стрешнево\n" ,
            "29. Балтийская\n" ,
            "30. Коптево\n" ,
            "31. Лихоборы\n" ,
            "Некрасовская.ветка (15)\n" ,
            "1.  Лефортово\n" ,
            "2.  Авиамоторная\n" ,
            "3.  Нижегородская\n" ,
            "4.  Стахановская\n" ,
            "5.  Окская\n" ,
            "6.  Юго-Восточная\n" ,
            "7.  Косино\n" ,
            "8.  Улица Дмитриевского\n" ,
            "9.  Лухмановская\n" ,
            "10. Некрасовка"
    };

    public static String[] getStr() {
        return str;
    }
}
