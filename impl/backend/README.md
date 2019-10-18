
## Wymagania:

* `Python 3`
* `pip` 
* `PostgreSQL` (z utworzoną bazą o nazwie "paum")

## Instalacja

O ile nic nie pomyliłem wystarczy jak puścisz `install.bat`. Jeżeli pomyliłem to wtedy trzeba kolejno:
* Założyć vitual enviroment najlepiej w tym folderze - komenda:
```bash
virtualenv -p python env
```
* Zainstalować dependencies - komenda:
```bash
pip install ./working_hours/requirements.txt
```
* Utworzyć baze o nazwie ```paum``` w postgresie - to już jak kto woli Pg admin albo konsola

* Wykonać migracje na bazie:
```bash
cd ./working_hours
python manage.py setup
```
## Uruchomienie
Całość uruchamiamy poprzez:
```
{scieżka_do_env}/Scripts/python {scieżka_do_folderu_z_settings.py}/manage.py runserver 8080
```

U mnie to będzie:
```bash
./env/Scripts/python ./working_hours/manage.py runserver 8080
```

## Domyślni użytkownicy

Automatycznie przy komendzie `setup` utworzeni zostaną trzej użytkownicy:
* Administrator: (u: `admin` p: `admin`) - może wszystko
* Pracodawca: (u: `pracodawca` p: `pracodawca`)
* Administrator: (u: `pracownik` p: `pracownik`)