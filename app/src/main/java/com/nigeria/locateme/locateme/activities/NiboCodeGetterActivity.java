package com.nigeria.locateme.locateme.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.nigeria.locateme.locateme.R;
import com.nigeria.locateme.locateme.entities.NiboUser;
import com.nigeria.locateme.locateme.entities.Preferences;

public class NiboCodeGetterActivity extends AppCompatActivity   { //implements View.OnClickListener, View.OnFocusChangeListener

    private static final String TAG = NiboCodeGetterActivity.class.getSimpleName();

    AutoCompleteTextView edtState,edtLGA,edtStreet,edtHouseAddress,edtEmail,edtPhoneNumber;
    Button butSav;

    //private SharedPreferences pref;

    private Preferences pref;

    private String choosenBuilding = "Please Choose Your Building Type";
    private String choosenState,choosenLGA = "Please Choose Your State";
    private Spinner buildingTypeSpinner,spinnerState,spinnerLGA;
    //private NiboUser niboUser;

    ArrayAdapter<CharSequence> adapterStates,adapterLGA,adapterLGAAbia,adapterLGAAdamawa,adapterLGAAkwaIbom,adapterLGAAnambra,adapterLGABauchi,adapterLGABayelsa,adapterLGABenue,adapterLGABorno,adapterLGACrossRiver,
            adapterLGADelta,adapterLGAEbonyi,adapterLGAEdo,adapterLGAEkiti,adapterLGAEnugu,adapterLGAGombe,adapterLGAImo,adapterLGAJigawa,adapterLGAKaduna,
            adapterLGAKano,adapterLGAKatsina,adapterLGAKebbi,
            adapterLGAKogi,adapterLGAKwara,adapterLGALagos,adapterLGANasarawa,adapterLGANiger,adapterLGAOgun,adapterLGAOndo,adapterLGAOsun,adapterLGAOyo,adapterLGAPlateau
            ,adapterLGARivers,adapterLGASokoto,adapterLGATaraba,adapterLGAYobe,adapterLGAZamfara,adapterLGAFCT,buildingTypeAdapter;



    String[] arrayLGA = {"PLEASE SELECT YOUR LOCAL GOVERNMENT"};

    String[] arrayLGAAbia = {"Please Select Your Local Government","Aba North","Aba South","Arochukwu","Bende","Isiala Ngwa South","Ikwuano","Isiala","Ngwa North","Isukwuato","Ukwa West","Ukwa East","Umuahia","Umuahia South"};


    String[] arrayLGAAdamawa = {"Please Select Your Local Government","Demsa", "Fufore", "Ganaye", "Gireri", "Gombi", "Guyuk", "Hong", "Jada", "Lamurde", "Madagali", "Maiha ", "Mayo-Belwa", "Michika", "Mubi North", "Mubi South", "Numan", "Shelleng", "Song", "Toungo", "Yola North", "Yola South"};

    String[] arrayLGAAkwaIbom = {"Please Select Your Local Government","Abak", "Eastern Obolo", "Eket", "Esit Eket", "Essien Udim", "Etim Ekpo", "Etinan", "Ibeno", "Ibesikpo Asutan", "Ibiono Ibom", "Ika", "Ikono", "Ikot Abasi", "Ikot Ekpene", "Ini", "Itu", "Mbo", "Mkpat Enin", "Nsit Atai", "Nsit Ibom", "Nsit Ubium", "Obot Akara", "Okobo", "Onna", "Oron ", "Oruk Anam", "Udung Uko", "Ukanafun", "Uruan", "Urue-Offong/Oruko", "Uyo"};

    String[] arrayLGAAnambra = {"Please Select Your Local Government","Aguata", "Anambra East", "Anambra West", "Anaocha", "Awka North", "Awka South", "Ayamelum", "Dunukofia", "Ekwusigo", "Idemili North", "Idemili south", "Ihiala", "Njikoka", "Nnewi North", "Nnewi South", "Ogbaru", "Onitsha North", "Onitsha South", "Orumba North", "Orumba South", "Oyi"};

    String[] arrayLGABauchi = {"Please Select Your Local Government","Alkaleri", "Bauchi", "Bogoro", "Damban", "Darazo", "Dass", "Ganjuwa", "Giade", "Itas/Gadau", "Jama'are", "Katagum", "Kirfi", "Misau", "Ningi", "Shira", "Tafawa-Balewa", "Toro", "Warji", "Zaki"};

    String[] arrayLGABayelsa = {"Please Select Your Local Government","Brass", "Ekeremor", "Kolokuma/Opokuma", "Nembe", "Ogbia", "Sagbama", "Southern Jaw", "Yenegoa "};

    String[] arrayLGABenue = {"Please Select Your Local Government","Ado", "Agatu", "Apa", "Buruku", "Gboko", "Guma", "Gwer East", "Gwer West", "Katsina-Ala", "Konshisha", "Kwande", "Logo", "Makurdi", "Obi", "Ogbadibo", "Oju", "Okpokwu", "Ohimini", "Oturkpo", "Tarka", "Ukum", "Ushongo", "Vandeikya"};

    String[] arrayLGABorno = {"Please Select Your Local Government","Abadam", "Askira/Uba", "Bama", "Bayo", "Biu", "Chibok", "Damboa", "Dikwa", "Gubio", "Guzamala", "Gwoza", "Hawul", "Jere", "Kaga", "Kala/Balge", "Konduga", "Kukawa", "Kwaya Kusar", "Mafa", "Magumeri", "Maiduguri", "Marte", "Mobbar", "Monguno", "Ngala", "Nganzai", "Shani"};

    String[] arrayLGACrossRiver = {"Please Select Your Local Government","Akpabuyo", "Odukpani", "Akamkpa", "Biase", "Abi", "Ikom", "Yarkur", "Obubra", "Boki", "Ogoja", "Yala", "Obanliku", "Obudu", "Calabar South", "Etung", "Bekwara", "Bakassi", "Calabar Municipality"};

    String[] arrayLGADelta = {"Please Select Your Local Government","Oshimili", "Aniocha", "Aniocha South", "Ika South", "Ika North-East", "Ndokwa West", "Ndokwa East", "Isoko south", "Isoko North", "Bomadi", "Burutu", "Ughelli South", "Ughelli North", "Ethiope West", "Ethiope East", "Sapele", "Okpe", "Warri North", "Warri South", "Uvwie", "Udu", "Warri Central", "Ukwani", "Oshimili North", "Patani"};

    String[] arrayLGAEbonyi = {"Please Select Your Local Government","Afikpo South", "Afikpo North", "Onicha", "Ohaozara", "Abakaliki", "Ishielu", "lkwo", "Ezza", "Ezza South", "Ohaukwu", "Ebonyi", "Ivo"};

    String[] arrayLGAEdo = {"Please Select Your Local Government","Esan North-East", "Esan Central", "Esan West", "Egor", "Ukpoba", "Central", "Etsako Central", "Igueben", "Oredo", "Ovia SouthWest", "Ovia South-East", "Orhionwon", "Uhunmwonde", "Etsako East ", "Esan South-East"};

    String[] arrayLGAEkiti = {"Please Select Your Local Government","Ado", "Ekiti-East", "Ekiti-West ", "Emure/Ise/Orun", "Ekiti South-West", "Ikare", "Irepodun", "Ijero, ", "Ido/Osi", "Oye", "Ikole", "Moba", "Gbonyin", "Efon", "Ise/Orun", "Ilejemeje"};

    String[] arrayLGAEnugu = {"Please Select Your Local Government","Enugu South, ", "Igbo-Eze South", "Enugu North", "Nkanu", "Udi Agwu", "Oji-River", "Ezeagu", "IgboEze North", "Isi-Uzo", "Nsukka", "Igbo-Ekiti", "Uzo-Uwani", "Enugu Eas", "Aninri", "Nkanu East", "Udenu"};

    String[] arrayLGAGombe = {"Please Select Your Local Government","Akko", "Balanga", "Billiri", "Dukku", "Kaltungo", "Kwami", "Shomgom", "Funakaye", "Gombe", "Nafada/Bajoga ", "Yamaltu/Delta. "};

    String[] arrayLGAImo = {"Please Select Your Local Government","Aboh-Mbaise", "Ahiazu-Mbaise", "Ehime-Mbano", "Ezinihitte", "Ideato North", "Ideato South", "Ihitte/Uboma", "Ikeduru", "Isiala Mbano", "Isu", "Mbaitoli", "Mbaitoli", "Ngor-Okpala", "Njaba", "Nwangele", "Nkwerre", "Obowo", "Oguta", "Ohaji/Egbema", "Okigwe", "Orlu", "Orsu", "Oru East", "Oru West", "Owerri-Municipal", "Owerri North", "Owerri West "};

    String[] arrayLGAJigawa = {"Please Select Your Local Government","Auyo", "Babura", "Birni Kudu", "Biriniwa", "Buji", "Dutse", "Gagarawa", "Garki", "Gumel", "Guri", "Gwaram", "Gwiwa", "Hadejia", "Jahun", "Kafin Hausa", "Kaugama Kazaure", "Kiri Kasamma", "Kiyawa", "Maigatari", "Malam Madori", "Miga", "Ringim", "Roni", "Sule-Tankarkar", "Taura ", "Yankwashi "};

    String[] arrayLGAKaduna = {"Please Select Your Local Government","Birni-Gwari", "Chikun", "Giwa", "Igabi", "Ikara", "jaba", "Jema'a", "Kachia", "Kaduna North", "Kaduna South", "Kagarko", "Kajuru", "Kaura", "Kauru", "Kubau", "Kudan", "Lere", "Makarfi", "Sabon-Gari", "Sanga", "Soba", "Zango-Kataf", "Zaria "};

    String[] arrayLGAKano = {"Please Select Your Local Government","Ajingi", "Albasu", "Bagwai", "Bebeji", "Bichi", "Bunkure", "Dala", "Dambatta", "Dawakin Kudu", "Dawakin Tofa", "Doguwa", "Fagge", "Gabasawa", "Garko", "Garum", "Mallam", "Gaya", "Gezawa", "Gwale", "Gwarzo", "Kabo", "Kano Municipal", "Karaye", "Kibiya", "Kiru", "kumbotso", "Kunchi", "Kura", "Madobi", "Makoda", "Minjibir", "Nasarawa", "Rano", "Rimin Gado", "Rogo", "Shanono", "Sumaila", "Takali", "Tarauni", "Tofa", "Tsanyawa", "Tudun Wada", "Ungogo", "Warawa", "Wudil"};

    String[] arrayLGAKatsina = {"Please Select Your Local Government","Bakori", "Batagarawa", "Batsari", "Baure", "Bindawa", "Charanchi", "Dandume", "Danja", "Dan Musa", "Daura", "Dutsi", "Dutsin-Ma", "Faskari", "Funtua", "Ingawa", "Jibia", "Kafur", "Kaita", "Kankara", "Kankia", "Katsina", "Kurfi", "Kusada", "Mai'Adua", "Malumfashi", "Mani", "Mashi", "Matazuu", "Musawa", "Rimi", "Sabuwa", "Safana", "Sandamu", "Zango "};

    String[] arrayLGAKebbi = {"Please Select Your Local Government","Aleiro", "Arewa-Dandi", "Argungu", "Augie", "Bagudo", "Birnin Kebbi", "Bunza", "Dandi ", "Fakai", "Gwandu", "Jega", "Kalgo ", "Koko/Besse", "Maiyama", "Ngaski", "Sakaba", "Shanga", "Suru", "Wasagu/Danko", "Yauri", "Zuru "};

    String[] arrayLGAKogi = {"Please Select Your Local Government","Adavi", "Ajaokuta", "Ankpa", "Bassa", "Dekina", "Ibaji", "Idah", "Igalamela-Odolu", "Ijumu", "Kabba/Bunu", "Kogi", "Lokoja", "Mopa-Muro", "Ofu", "Ogori/Mangongo", "Okehi", "Okene", "Olamabolo", "Omala", "Yagba East ", "Yagba West"};

    String[] arrayLGAKwara = {"Please Select Your Local Government","Asa", "Baruten", "Edu", "Ekiti", "Ifelodun", "Ilorin East", "Ilorin West", "Irepodun", "Isin", "Kaiama", "Moro", "Offa", "Oke-Ero", "Oyun", "Pategi "};

    String[] arrayLGALagos = {"Please Select Your Local Government","Agege", "Ajeromi-Ifelodun", "Alimosho", "Amuwo-Odofin", "Apapa", "Badagry", "Epe", "Eti-Osa", "Ibeju/Lekki", "Ifako-Ijaye ", "Ikeja", "Ikorodu", "Kosofe", "Lagos Island", "Lagos Mainland", "Mushin", "Ojo", "Oshodi-Isolo", "Shomolu", "Surulere", "Yaba"};

    String[] arrayLGANasarawa = {"Please Select Your Local Government","Akwanga", "Awe", "Doma", "Karu", "Keana", "Keffi", "Kokona", "Lafia", "Nasarawa", "Nasarawa-Eggon", "Obi", "Toto", "Wamba "};

    String[] arrayLGANiger = {"Please Select Your Local Government","Agaie", "Agwara", "Bida", "Borgu", "Bosso", "Chanchaga", "Edati", "Gbako", "Gurara", "Katcha", "Kontagora ", "Lapai", "Lavun", "Magama", "Mariga", "Mashegu", "Mokwa", "Muya", "Pailoro", "Rafi", "Rijau", "Shiroro", "Suleja", "Tafa", "Wushishi"};

    String[] arrayLGAOgun = {"Please Select Your Local Government","Abeokuta North", "Abeokuta South", "Ado-Odo/Ota", "Egbado North", "Egbado South", "Ewekoro", "Ifo", "Ijebu East", "Ijebu North", "Ijebu North East", "Ijebu Ode", "Ikenne", "Imeko-Afon", "Ipokia", "Obafemi-Owode", "Ogun Waterside", "Odeda", "Odogbolu", "Remo North", "Shagamu"};

    String[] arrayLGAOndo = {"Please Select Your Local Government","Akoko North East", "Akoko North West", "Akoko South Akure East", "Akoko South West", "Akure North", "Akure South", "Ese-Odo", "Idanre", "Ifedore", "Ilaje", "Ile-Oluji", "Okeigbo", "Irele", "Odigbo", "Okitipupa", "Ondo East", "Ondo West", "Ose", "Owo "};

    String[] arrayLGAOsun = {"Please Select Your Local Government","Aiyedade", "Aiyedire", "Atakumosa East", "Atakumosa West", "Boluwaduro", "Boripe", "Ede North", "Ede South", "Egbedore", "Ejigbo", "Ife Central", "Ife East", "Ife North", "Ife South", "Ifedayo", "Ifelodun", "Ila", "Ilesha East", "Ilesha West", "Irepodun", "Irewole", "Isokan", "Iwo", "Obokun", "Odo-Otin", "Ola-Oluwa", "Olorunda", "Oriade", "Orolu", "Osogbo"};

    String[] arrayLGAOyo = {"Please Select Your Local Government","Afijio", "Akinyele", "Atiba", "Atigbo", "Egbeda", "IbadanCentral", "Ibadan North", "Ibadan North West", "Ibadan South East", "Ibadan South West", "Ibarapa Central", "Ibarapa East", "Ibarapa North", "Ido", "Irepo", "Iseyin", "Itesiwaju", "Iwajowa", "Kajola", "Lagelu Ogbomosho North", "Ogbmosho South", "Ogo Oluwa", "Olorunsogo", "Oluyole", "Ona-Ara", "Orelope", "Ori Ire", "Oyo East", "Oyo West", "Saki East", "Saki West", "Surulere"};


    String[] arrayLGAPlateau = {"Please Select Your Local Government","Barikin Ladi", "Bassa", "Bokkos", "Jos East", "Jos North", "Jos South", "Kanam", "Kanke", "Langtang North", "Langtang South", "Mangu", "Mikang", "Pankshin", "Qua'an Pan", "Riyom", "Shendam", "Wase"};

    String[] arrayLGARivers = {"Please Select Your Local Government","Abua/Odual", "Ahoada East", "Ahoada West", "Akuku Toru", "Andoni", "Asari-Toru", "Bonny", "Degema", "Emohua", "Eleme", "Etche", "Gokana", "Ikwerre", "Khana", "Obia/Akpor", "Ogba/Egbema/Ndoni", "Ogu/Bolo", "Okrika", "Omumma", "Opobo/Nkoro", "Oyigbo", "Port-Harcourt", "Tai "};

    String[] arrayLGASokoto = {"Please Select Your Local Government","Binji", "Bodinga", "Dange-shnsi", "Gada", "Goronyo", "Gudu", "Gawabawa", "Illela", "Isa", "Kware", "kebbe", "Rabah", "Sabon birni", "Shagari", "Silame", "Sokoto North", "Sokoto South", "Tambuwal", "Tqngaza", "Tureta", "Wamako", "Wurno", "Yabo"};

    String[] arrayLGATaraba = {"Ardo-kola", "Bali", "Donga", "Gashaka", "Cassol", "Ibi", "Jalingo", "Karin-Lamido", "Kurmi", "Lau", "Sardauna", "Takum", "Ussa", "Wukari", "Yorro", "Zing"};

    String[] arrayLGAYobe = {"Please Select Your Local Government","Bade", "Bursari", "Damaturu", "Fika", "Fune", "Geidam", "Gujba", "Gulani", "Jakusko", "Karasuwa", "Karawa", "Machina", "Nangere", "Nguru Potiskum", "Tarmua", "Yunusari", "Yusufari"};

    String[] arrayLGAZamfara = {"Please Select Your Local Government","Anka ", "Bakura", "Birnin Magaji", "Bukkuyum", "Bungudu", "Gummi", "Gusau", "Kaura", "Namoda", "Maradun", "Maru", "Shinkafi", "Talata Mafara", "Tsafe", "Zurmi "};

    String[] arrayLGAFCT = {"Please Select Your Local Government","Gwagwalada", "Kuje", "Abaji", "Abuja Municipal", "Bwari", "Kwali"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nibo_code_getter);


        pref = new Preferences(this);
        //niboUser = pref.getUserDetails();


        //edtState = (AutoCompleteTextView) findViewById(R.id.editTextState);
        spinnerState = (Spinner)findViewById(R.id.spinnerState);
        //edtLGA = (AutoCompleteTextView) findViewById(R.id.editTextLGA);
        spinnerLGA = (Spinner)findViewById(R.id.spinnerLGA);
        edtStreet = (AutoCompleteTextView) findViewById(R.id.editTextStreetName);
        edtHouseAddress = (AutoCompleteTextView) findViewById(R.id.editTextHouseAddress);
        buildingTypeSpinner = (Spinner) findViewById(R.id.spinnerBuildingType);
        edtEmail = (AutoCompleteTextView) findViewById(R.id.editTextEmail);
        edtPhoneNumber = (AutoCompleteTextView) findViewById(R.id.editTextPhoneNumber);
        butSav = (Button) findViewById(R.id.button_saveNewBuilding);

        butSav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveToDatabase();
            }
        });


        buildingTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.building_type, android.R.layout.simple_spinner_item);



        buildingTypeSpinner.setAdapter(buildingTypeAdapter);

        buildingTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                choosenBuilding = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });






        adapterStates = ArrayAdapter.createFromResource(this,
                R.array.states, android.R.layout.simple_spinner_item);

        adapterLGA = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGA);

        adapterLGAAbia = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGAAbia);

        adapterLGAAdamawa = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGAAdamawa);

        adapterLGAAkwaIbom = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGAAkwaIbom);

        adapterLGAAnambra = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGAAnambra);

        adapterLGABauchi = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGABauchi);

        adapterLGABayelsa = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGABayelsa);

        adapterLGABenue = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGABenue);

        adapterLGABorno = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGABorno);

        adapterLGACrossRiver = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGACrossRiver);

        adapterLGADelta = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGADelta);

        adapterLGAEbonyi = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGAEbonyi);

        adapterLGAEdo = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGAEdo);

        adapterLGAEkiti = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGAEkiti);

        adapterLGAEnugu = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGAEnugu);

        adapterLGAFCT = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGAFCT);

        adapterLGAGombe = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGAGombe);

        adapterLGAImo = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGAImo);

        adapterLGAJigawa = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGAJigawa);

        adapterLGAKaduna = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGAKaduna);

        adapterLGAKano = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGAKano);

        adapterLGAKatsina = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGAKatsina);

        adapterLGAKebbi = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGAKebbi);

        adapterLGAKogi = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGAKogi);

        adapterLGAKwara = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGAKwara);

        adapterLGALagos = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGALagos);

        adapterLGANasarawa = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGANasarawa);

        adapterLGANiger = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGANiger);

        adapterLGAOgun = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGAOgun);

        adapterLGAOndo = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGAOndo);

        adapterLGAOsun = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGAOsun);

        adapterLGAOyo = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGAOyo);

        adapterLGAPlateau = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGAPlateau);

        adapterLGARivers = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGARivers);

        adapterLGASokoto = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGASokoto);

        adapterLGATaraba = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGATaraba);

        adapterLGAYobe = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGAYobe);

        adapterLGAZamfara = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item , arrayLGAZamfara);





        //edtState.setAdapter(adapterStates);
        spinnerState.setAdapter(adapterStates);
        spinnerLGA.setAdapter(adapterLGA);


        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                choosenState = parent.getItemAtPosition(position).toString();
                setLocalGovernmentSpinner(choosenState);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        spinnerLGA.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                choosenLGA = parent.getItemAtPosition(position).toString();
                if(choosenLGA.contains("Please")){
                    Toast.makeText(NiboCodeGetterActivity.this, "Please Select A Local Government", Toast.LENGTH_SHORT).show();
                }
                //setLocalGovernmentSpinner(choosenState);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //  edtLGA.setOnClickListener(this);

        //edtLGA.setOnFocusChangeListener(this);


    }

    public void setLocalGovernmentSpinner(String st){
        String state = st;

        if(state.length() <1 ){
            Toast.makeText(this, "Please Select A State", Toast.LENGTH_SHORT).show();
        }else if( state.contains("PLEASE") ){
            spinnerLGA.setAdapter(adapterLGA);
        }else if( state.contains("Abia") ){
            spinnerLGA.setAdapter(adapterLGAAbia);
        }else if( state.contains("Adamawa") ){
            spinnerLGA.setAdapter(adapterLGAAdamawa);
        }else if( state.contains("Akwa") ){
            spinnerLGA.setAdapter(adapterLGAAkwaIbom);
        }else if( state.contains("Anambra") ){
            spinnerLGA.setAdapter(adapterLGAAnambra);
        }else if( state.contains("Bauchi") ){
            spinnerLGA.setAdapter(adapterLGABauchi);
        }else if( state.contains("Bayelsa") ){
            spinnerLGA.setAdapter(adapterLGABayelsa);
        }else if( state.contains("Benue") ){
            spinnerLGA.setAdapter(adapterLGABenue);
        }else if( state.contains("Borno") ){
            spinnerLGA.setAdapter(adapterLGABorno);
        }else if( state.contains("Cross") ){
            spinnerLGA.setAdapter(adapterLGACrossRiver);
        }else if( state.contains("Delta") ){
            spinnerLGA.setAdapter(adapterLGADelta);
        }else if( state.contains("Ebonyi") ){
            spinnerLGA.setAdapter(adapterLGAEbonyi);
        }else if( state.contains("Edo") ){
            spinnerLGA.setAdapter(adapterLGAEdo);
        }else if( state.contains("Ekiti") ){
            spinnerLGA.setAdapter(adapterLGAEkiti);
        }else if( state.contains("Enugu") ){
            spinnerLGA.setAdapter(adapterLGAEnugu);
        }else if( state.contains("FCT") ){
            spinnerLGA.setAdapter(adapterLGAFCT);
        }else if( state.contains("Federal") ){
            spinnerLGA.setAdapter(adapterLGAFCT);
        }else if( state.contains("Gombe") ){
            spinnerLGA.setAdapter(adapterLGAGombe);
        }else if( state.contains("Imo") ){
            spinnerLGA.setAdapter(adapterLGAImo);
        }else if( state.contains("Jigawa") ){
            spinnerLGA.setAdapter(adapterLGAJigawa);
        }else if( state.contains("Kaduna") ){
            spinnerLGA.setAdapter(adapterLGAKaduna);
        }else if( state.contains("Kano") ){
            spinnerLGA.setAdapter(adapterLGAKano);
        }else if( state.contains("Katsina") ){
            spinnerLGA.setAdapter(adapterLGAKatsina);
        }else if( state.contains("Kebbi") ){
            spinnerLGA.setAdapter(adapterLGAKebbi);
        }else if( state.contains("Kogi") ){
            spinnerLGA.setAdapter(adapterLGAKogi);
        }else if( state.contains("Kwara") ){
            spinnerLGA.setAdapter(adapterLGAKwara);
        }else if( state.contains("Lagos") ){
            spinnerLGA.setAdapter(adapterLGALagos);
        }else if( state.contains("Nasarawa") ){
            spinnerLGA.setAdapter(adapterLGANasarawa);
        }else if( state.contains("Niger") ){
            spinnerLGA.setAdapter(adapterLGANiger);
        }else if( state.contains("Ogun") ){
            spinnerLGA.setAdapter(adapterLGAOgun);
        }else if( state.contains("Ondo") ){
            spinnerLGA.setAdapter(adapterLGAOndo);
        }else if( state.contains("Osun") ){
            spinnerLGA.setAdapter(adapterLGAOsun);
        }else if( state.contains("Oyo") ){
            spinnerLGA.setAdapter(adapterLGAOyo);
        }else if( state.contains("Plateau") ){
            spinnerLGA.setAdapter(adapterLGAPlateau);
        }else if( state.contains("Rivers") ){
            spinnerLGA.setAdapter(adapterLGARivers);
        }else if( state.contains("Sokoto") ){
            spinnerLGA.setAdapter(adapterLGASokoto);
        }else if( state.contains("Taraba") ){
            spinnerLGA.setAdapter(adapterLGATaraba);
        }else if( state.contains("Yobe") ){
            spinnerLGA.setAdapter(adapterLGAYobe);
        }else if( state.contains("Zamfara") ){
            spinnerLGA.setAdapter(adapterLGAZamfara);
        }
    }

   /* @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View view) {
        String state = edtState.getText().toString();

        if(state.length() <1 ){
            Toast.makeText(this, "Please Select A State", Toast.LENGTH_SHORT).show();
        }else if( state.contains("Abia") ){
            edtLGA.setAdapter(adapterLGAAbia);
        }else if( state.contains("Adamawa") ){
            edtLGA.setAdapter(adapterLGAAdamawa);
        }else if( state.contains("Akwa") ){
            edtLGA.setAdapter(adapterLGAAkwaIbom);
        }else if( state.contains("Anambra") ){
            edtLGA.setAdapter(adapterLGAAnambra);
        }else if( state.contains("Bauchi") ){
            edtLGA.setAdapter(adapterLGABauchi);
        }else if( state.contains("Bayelsa") ){
            edtLGA.setAdapter(adapterLGABayelsa);
        }else if( state.contains("Benue") ){
            edtLGA.setAdapter(adapterLGABenue);
        }else if( state.contains("Borno") ){
            edtLGA.setAdapter(adapterLGABorno);
        }else if( state.contains("Cross") ){
            edtLGA.setAdapter(adapterLGACrossRiver);
        }else if( state.contains("Delta") ){
            edtLGA.setAdapter(adapterLGADelta);
        }else if( state.contains("Ebonyi") ){
            edtLGA.setAdapter(adapterLGAEbonyi);
        }else if( state.contains("Edo") ){
            edtLGA.setAdapter(adapterLGAEdo);
        }else if( state.contains("Ekiti") ){
            edtLGA.setAdapter(adapterLGAEkiti);
        }else if( state.contains("Enugu") ){
            edtLGA.setAdapter(adapterLGAEnugu);
        }else if( state.contains("FCT") ){
            edtLGA.setAdapter(adapterLGAFCT);
        }else if( state.contains("Federal") ){
            edtLGA.setAdapter(adapterLGAFCT);
        }else if( state.contains("Gombe") ){
            edtLGA.setAdapter(adapterLGAGombe);
        }else if( state.contains("Imo") ){
            edtLGA.setAdapter(adapterLGAImo);
        }else if( state.contains("Jigawa") ){
            edtLGA.setAdapter(adapterLGAJigawa);
        }else if( state.contains("Kaduna") ){
            edtLGA.setAdapter(adapterLGAKaduna);
        }else if( state.contains("Kano") ){
            edtLGA.setAdapter(adapterLGAKano);
        }else if( state.contains("Katsina") ){
            edtLGA.setAdapter(adapterLGAKatsina);
        }else if( state.contains("Kebbi") ){
            edtLGA.setAdapter(adapterLGAKebbi);
        }else if( state.contains("Kogi") ){
            edtLGA.setAdapter(adapterLGAKogi);
        }else if( state.contains("Kwara") ){
            edtLGA.setAdapter(adapterLGAKwara);
        }else if( state.contains("Lagos") ){
            edtLGA.setAdapter(adapterLGALagos);
        }else if( state.contains("Nasarawa") ){
            edtLGA.setAdapter(adapterLGANasarawa);
        }else if( state.contains("Niger") ){
            edtLGA.setAdapter(adapterLGANiger);
        }else if( state.contains("Ogun") ){
            edtLGA.setAdapter(adapterLGAOgun);
        }else if( state.contains("Ondo") ){
            edtLGA.setAdapter(adapterLGAOndo);
        }else if( state.contains("Osun") ){
            edtLGA.setAdapter(adapterLGAOsun);
        }else if( state.contains("Oyo") ){
            edtLGA.setAdapter(adapterLGAOyo);
        }else if( state.contains("Plateau") ){
            edtLGA.setAdapter(adapterLGAPlateau);
        }else if( state.contains("Rivers") ){
            edtLGA.setAdapter(adapterLGARivers);
        }else if( state.contains("Sokoto") ){
            edtLGA.setAdapter(adapterLGASokoto);
        }else if( state.contains("Taraba") ){
            edtLGA.setAdapter(adapterLGATaraba);
        }else if( state.contains("Yobe") ){
            edtLGA.setAdapter(adapterLGAYobe);
        }else if( state.contains("Zamfara") ){
            edtLGA.setAdapter(adapterLGAZamfara);
        }

    }

    @Override
    public void onFocusChange(View view, boolean b) {
        String state = edtState.getText().toString();

        if(state.length() <1 ){
            Toast.makeText(this, "Please Select A State", Toast.LENGTH_SHORT).show();
        }else if( state.contains("Abia") ){
            edtLGA.setAdapter(adapterLGAAbia);
        }else if( state.contains("Adamawa") ){
            edtLGA.setAdapter(adapterLGAAdamawa);
        }else if( state.contains("Akwa") ){
            edtLGA.setAdapter(adapterLGAAkwaIbom);
        }else if( state.contains("Anambra") ){
            edtLGA.setAdapter(adapterLGAAnambra);
        }else if( state.contains("Bauchi") ){
            edtLGA.setAdapter(adapterLGABauchi);
        }else if( state.contains("Bayelsa") ){
            edtLGA.setAdapter(adapterLGABayelsa);
        }else if( state.contains("Benue") ){
            edtLGA.setAdapter(adapterLGABenue);
        }else if( state.contains("Borno") ){
            edtLGA.setAdapter(adapterLGABorno);
        }else if( state.contains("Cross") ){
            edtLGA.setAdapter(adapterLGACrossRiver);
        }else if( state.contains("Delta") ){
            edtLGA.setAdapter(adapterLGADelta);
        }else if( state.contains("Ebonyi") ){
            edtLGA.setAdapter(adapterLGAEbonyi);
        }else if( state.contains("Edo") ){
            edtLGA.setAdapter(adapterLGAEdo);
        }else if( state.contains("Ekiti") ){
            edtLGA.setAdapter(adapterLGAEkiti);
        }else if( state.contains("Enugu") ){
            edtLGA.setAdapter(adapterLGAEnugu);
        }else if( state.contains("FCT") ){
            edtLGA.setAdapter(adapterLGAFCT);
        }else if( state.contains("Federal") ){
            edtLGA.setAdapter(adapterLGAFCT);
        }else if( state.contains("Gombe") ){
            edtLGA.setAdapter(adapterLGAGombe);
        }else if( state.contains("Imo") ){
            edtLGA.setAdapter(adapterLGAImo);
        }else if( state.contains("Jigawa") ){
            edtLGA.setAdapter(adapterLGAJigawa);
        }else if( state.contains("Kaduna") ){
            edtLGA.setAdapter(adapterLGAKaduna);
        }else if( state.contains("Kano") ){
            edtLGA.setAdapter(adapterLGAKano);
        }else if( state.contains("Katsina") ){
            edtLGA.setAdapter(adapterLGAKatsina);
        }else if( state.contains("Kebbi") ){
            edtLGA.setAdapter(adapterLGAKebbi);
        }else if( state.contains("Kogi") ){
            edtLGA.setAdapter(adapterLGAKogi);
        }else if( state.contains("Kwara") ){
            edtLGA.setAdapter(adapterLGAKwara);
        }else if( state.contains("Lagos") ){
            edtLGA.setAdapter(adapterLGALagos);
        }else if( state.contains("Nasarawa") ){
            edtLGA.setAdapter(adapterLGANasarawa);
        }else if( state.contains("Niger") ){
            edtLGA.setAdapter(adapterLGANiger);
        }else if( state.contains("Ogun") ){
            edtLGA.setAdapter(adapterLGAOgun);
        }else if( state.contains("Ondo") ){
            edtLGA.setAdapter(adapterLGAOndo);
        }else if( state.contains("Osun") ){
            edtLGA.setAdapter(adapterLGAOsun);
        }else if( state.contains("Oyo") ){
            edtLGA.setAdapter(adapterLGAOyo);
        }else if( state.contains("Plateau") ){
            edtLGA.setAdapter(adapterLGAPlateau);
        }else if( state.contains("Rivers") ){
            edtLGA.setAdapter(adapterLGARivers);
        }else if( state.contains("Sokoto") ){
            edtLGA.setAdapter(adapterLGASokoto);
        }else if( state.contains("Taraba") ){
            edtLGA.setAdapter(adapterLGATaraba);
        }else if( state.contains("Yobe") ){
            edtLGA.setAdapter(adapterLGAYobe);
        }else if( state.contains("Zamfara") ){
            edtLGA.setAdapter(adapterLGAZamfara);
        }
    }*/

    public void SaveToDatabase(){



        NiboUser phoneCon = pref.getUserDetails();
        String fullName = phoneCon.getFullName();


        //String state = edtState.getText().toString();
        String state = choosenState;
        //String localGov = edtLGA.getText().toString();
        String localGov = choosenLGA;
        String street = edtStreet.getText().toString();
        String houseAddress = edtHouseAddress.getText().toString();
        String email = edtEmail.getText().toString();
        String phoneNumber = edtPhoneNumber.getText().toString();



        if(!choosenState.contains("PLEASE") && !choosenLGA.contains("Please") && street.length() != 0 && houseAddress.length() != 0 && email.length() != 0 && phoneNumber.length() != 0 && !choosenBuilding.equals("Please Choose Building Type") ){
            Intent saveActivity = new Intent(NiboCodeGetterActivity.this, MapsActivity.class);
            saveActivity.putExtra("state",state);
            saveActivity.putExtra("localGov",localGov);
            saveActivity.putExtra("street",street);
            saveActivity.putExtra("houseAddress",houseAddress);
            saveActivity.putExtra("buildingType",choosenBuilding);
            saveActivity.putExtra("email",email);
            saveActivity.putExtra("phoneNumber",phoneNumber);
            saveActivity.putExtra("fullName",fullName);
            startActivity(saveActivity);
        }else{
            Toast.makeText(this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
        }


    }
}
