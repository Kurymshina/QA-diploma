# __Дипломный проект по профессии «Тестировщик»__

## Задание [https://github.com/netology-code/qa-diploma]("netology-code/qa-diploma") 
 Ключевая задача — *автоматизировать позитивные и негативные сценарии покупки тура*

## Документация
* План автоматизации тестирования []
* Отчётные документы по итогам автоматизированного тестирования []("Отчёт по итогам тестирования")
* Отчётные документы по итогам автоматизаци []("Отчёт об автоматизации")

## Описание приложения
![image](https://github.com/Kurymshina/QA-diploma/assets/127852172/e5ab3d89-891e-4a85-a3ac-a6c14795374a)

Приложение — это веб-сервис, который предлагает купить тур по определённой цене двумя способами:

* Обычная оплата по дебетовой карте.
* Уникальная технология: выдача кредита по данным банковской карты.


Само приложение не обрабатывает данные по картам, а пересылает их банковским сервисам:

* сервису платежей, далее Payment Gate;
* кредитному сервису, далее Credit Gate.
Приложение в собственной СУБД должно сохранять информацию о том, успешно ли был совершён платёж и каким способом. Данные карт при этом сохранять не допускается.
> в реальной жизни приложение не должно пропускать через себя данные карт, если у него нет PCI DSS
