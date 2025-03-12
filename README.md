Evidenca zaposlenih

Je aplikacija v katerem si lahko ogledamo vse podatke o zaposlenih v bazi, oddelke v podjetju ter nazive itd.
Lahko urejaš bazo in dodaš nove zaposlene, ter baza zabeleži kdaj se je kdo zaposlil in se mu je naziv spremenil.

Proces delanja aplikacije:

ER Diagram sem naredil v Toad Data Modeler. Naredil sem 5 entitet (tabel):
- Zaposleni (id, ime, priimek, email, telefon, kraj_id, naziv_id)
- Oddelek (ime, datum_ustanove)
- Naziv (ime, neto_placa)
- Zgodovina_zaposlitve (datum_zacetka, datum_konec_zaposlitve, zaposleni_id, oddelek_id)
- Kraji (ime, posta)

Zgodovina zaposlitve je mišljen kot arhiv, zato je povezava M:M med tabelo zaposleni in oddelek.

Aplikacijo sem naredil v IntelliJ IDEA 2024 in sem uporabljal Java programming language. Za preprosto oblikovanje User Interface sem naložil plugin Swing UI Designer.
Poleg tega sem tudi naredil Neon DB profil in tam sem svojo databazo vstavil. Podatke od NeonDB-ja sem shranil v .env datoteko iz katere bom podatke pridobil za povezavo med databazo in aplikacijo.
Naložil sem tudi module za Kotlin in sql, da knjižnice delujejo.

SQL Querye sem pisal v Neon SQL Editorju. Napisal sem strežniški podprogram s katerim lahko dodaš zaposlenega:

CREATE OR REPLACE FUNCTION dodaj_zaposlenega(
ime_z VARCHAR(20), 
priimek_z VARCHAR(30), 
email_z VARCHAR(90), 
telefon_z INTEGER,
kraj_z VARCHAR(20),
naziv_z VARCHAR(40),
oddelek_z VARCHAR(40)
) RETURNS VOID AS $$
DECLARE 
em_id INTEGER;
tel_id INTEGER;
BEGIN 
SELECT id INTO em_id FROM zaposleni WHERE email = email_z;
SELECT id INTO tel_id FROM zaposleni WHERE telefon = telefon_z;
                                        
IF (em_id IS NULL AND tel_id IS NULL) THEN 
INSERT INTO zaposleni (ime, priimek, email, telefon, kraji_id, naziv_id)
VALUES (ime_z, priimek_z, email_z, telefon_z, 
(SELECT id FROM kraji WHERE ime = kraj_z), 
(SELECT id FROM naziv WHERE ime = naziv_z));
                                                                                            
INSERT INTO zgodovina_zaposlitve(datum_zaposlitve, datum_konec_zaposlitve, zaposleni_id, oddelek_id)
VALUES (NOW(), NULL, (SELECT id FROM zaposleni WHERE ime = ime_z), (SELECT id FROM oddelek_id WHERE ime = oddelek_z));
END IF;
END;
$$ LANGUAGE 'plpgsql';

Ta funkcija preveri, če vnešen email ali telefonska številka že obstajata. Če ne, doda na novo zaposlenega ter podatke o njem. Ko je zaposleni narejen, v zgodovini zaposlitve tudi zabeleži, čas zaposlitve in v katerem oddelku dela.

Imam tudi 3 prožilce. Eden je namenjen temu, da preverja če ustvarjen oddelek že obstaja. Če obstaja, tega na novo ustvarjenega zbriše.

CREATE OR REPLACE FUNCTION preveri_oddelek()
RETURNS TRIGGER AS $$
declare cnt INT;
BEGIN
SELECT INTO cnt count(*) FROM oddelek WHERE ime = NEW.ime;

    IF cnt > 0 THEN
    
        DELETE FROM oddelek WHERE id = NEW.id;
        RETURN NULL;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER preveri_oddelek_trigger
BEFORE INSERT ON oddelek
FOR EACH ROW
EXECUTE FUNCTION preveri_oddelek();

Moj drugi prožilec je namenjen temu, da preveri, če se je zaposleni upokojil. Če se je, zabeleži konec njegove zaposlitve.

CREATE OR REPLACE FUNCTION update_datum_konec_zaposlitve()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.naziv_id = (SELECT id FROM naziv WHERE ime = 'Upokojen') THEN
        UPDATE zgodovina_zaposlitve
        SET datum_konec_zaposlitve = NOW()
        WHERE zaposleni_id = NEW.id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

Moj zadnji prožilec, je pa namenjen temu, da če zaposleni ima nov naziv, zabeleži novo zaposlitev kot ta naziv.

CREATE OR REPLACE FUNCTION insert_zgodovina_on_naziv_change()
RETURNS TRIGGER AS $$
DECLARE
    upokojen_id INT;
BEGIN
    SELECT id INTO upokojen_id FROM naziv WHERE ime = 'Upokojen';

    IF OLD.naziv_id IS DISTINCT FROM NEW.naziv_id AND NEW.naziv_id <> upokojen_id THEN

        INSERT INTO zgodovina_zaposlitve (zaposleni_id, oddelek_id, naziv_id, datum_zacetka_zaposlitve, datum_konec_zaposlitve)
        VALUES (NEW.id, NEW.oddelek_id, NEW.naziv_id, NOW(), NULL);
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trigger_insert_zgodovina_on_naziv_change
AFTER UPDATE OF naziv_id
ON zaposleni
FOR EACH ROW
EXECUTE FUNCTION insert_zgodovina_on_naziv_change();

V aplikaciji imaš combobox, s katerim izbereš tabelo. Imaš gumbe za izpis, printanje v Excelu in za izhod. Tudi imaš gumb za urejanje baze. To odpre novo okno, v katerem lahko dodaš novega zaposlenega, oddelka, izbrišeš zaposlenega pa za
posodobitev naziva. Za vse funkcije nad uporabnike je potreben email in telefon. 
