package com.bogomazz.MenuAvenue.Order.Checkout;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bogomazz.MenuAvenue.DataLoader;
import com.bogomazz.MenuAvenue.DatabaseLoader;
import com.bogomazz.MenuAvenue.Exception.NoNetworkException;
import com.bogomazz.MenuAvenue.MainPane.MainActivity;
import com.bogomazz.MenuAvenue.Order.FinishOrderActivity;
import com.bogomazz.MenuAvenue.R;
import com.bogomazz.MenuAvenue.User;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by andrey on 11/5/14.
 */
public class DeliveryFragment extends Fragment {
    private boolean isDeliveryInOneHour = true;
    private ToggleButton deliverInOneHour;
    private ToggleButton setOtherTime;
    private static String[] streets;
    private final static String POST_ADDRESS_URL = "http://bogomazz.com/flask/menuavenue/add";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.checkout_delivery_fragment, container, false);

        setUpUI(rootView);

        return rootView;
    }
//    @Override
//    public void onResume() {
//        super.onResume();
//
//    }

    private void setUpUI(View rootView) {
        TextView addressTitle = (TextView) rootView.findViewById(R.id.deliveryAddress);
        TextView cityName = (TextView) rootView.findViewById(R.id.cityName);
        EditText cityInput = (EditText) rootView.findViewById(R.id.city);
        TextView streetName = (TextView) rootView.findViewById(R.id.streetName);
        final AutoCompleteTextView streetInput = (AutoCompleteTextView)
                rootView.findViewById(R.id.street);
        TextView houseName = (TextView) rootView.findViewById(R.id.houseName);
        final EditText houseInput = (EditText) rootView.findViewById(R.id.house);
        TextView flatName = (TextView) rootView.findViewById(R.id.flatName);
        final EditText flat = (EditText) rootView.findViewById(R.id.flat);

        TextView informationTitle = (TextView) rootView.findViewById(R.id.information);
        TextView emailName = (TextView) rootView.findViewById(R.id.emailName);
        final EditText emailInput = (EditText) rootView.findViewById(R.id.email);
        TextView phoneName = (TextView) rootView.findViewById(R.id.phoneNumberName);
        final EditText phoneInput = (EditText) rootView.findViewById(R.id.phoneNumber);

        deliverInOneHour = (ToggleButton) rootView.findViewById(R.id.inOneHour);
        setOtherTime = (ToggleButton) rootView.findViewById(R.id.otherTime);

        TextView commentTitle = (TextView) rootView.findViewById(R.id.commentTitle);
        final EditText commentInput = (EditText) rootView.findViewById(R.id.comment);

        Button submitOrder = (Button) rootView.findViewById(R.id.submitOrder);

        cityName.setText(R.string.city);
        cityInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        cityInput.setText(getResources().getText(R.string.kiev));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, streets);

        streetInput.setAdapter(adapter);

        streetName.setText(R.string.street);
        houseName.setText(R.string.house);
        flatName.setText(R.string.flat);
        if (User.email != null) {
            rootView.findViewById(R.id.emailId).setVisibility(View.GONE);
        }
        ((EditText) rootView.findViewById(R.id.phoneNumber)).setText(User.phone);
        emailName.setText(R.string.email);
        phoneName.setText(R.string.phone);
        deliverInOneHour.setText(R.string.inOnHour);
        deliverInOneHour.setTextOn(getResources().getString(R.string.inOnHour));
        deliverInOneHour.setTextOff(getResources().getString(R.string.inOnHour));
        setOtherTime.setText(getResources().getString(R.string.otherTime));
        setOtherTime.setTextOn(getResources().getString(R.string.otherTime));
        setOtherTime.setTextOff(getResources().getString(R.string.otherTime));
        commentTitle.setText(R.string.comment);
        submitOrder.setText(R.string.submitOrder);

        submitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!DataLoader.isNetworkAvailable(getActivity())) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), getResources().getText(R.string.selectIngredients), Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    if (User.email == null && User.userId == null) {
                        User.initUser(emailInput.toString(), phoneInput.toString());
                    }
                        //90
                    Intent intent = new Intent(getActivity().getApplicationContext(), FinishOrderActivity.class);
                    startActivity(intent);
                    String phoneNumber = phoneInput.getText().toString();
                    String deliveryTime = null;

                    List<NameValuePair> params = new ArrayList<NameValuePair>(2);
                    params.add(new BasicNameValuePair("table", "address"));
                    params.add(new BasicNameValuePair("user_id", User.userId));
                    String encodedAddressStreet = DataLoader.convertToUTF8(streetInput.getText().toString());
                    params.add(new BasicNameValuePair("address_street", encodedAddressStreet));
                    Log.e("address_street", encodedAddressStreet);
                    params.add(new BasicNameValuePair("address_home", houseInput.getText().toString()));
                    params.add(new BasicNameValuePair("address_flat", flat.getText().toString()));
                    params.add(new BasicNameValuePair("address_floor", flat.getText().toString().charAt(0)+""));

                    try {
                        String addressId = new PostAddress().execute(params).get();
                        String comment = DataLoader.convertToUTF8(commentInput.getText().toString());
                        Log.e(addressId+"", "AddressId");
                        ((CheckoutActivity) getActivity()).postOrder(addressId, phoneNumber, comment, isDeliveryInOneHour);
                    } catch (InterruptedException e) {
                        Log.e("Interrupted exception", e.toString());
                    } catch (ExecutionException e) {
                        Log.e("Execution exception", e.toString());
                    }
                    getActivity().finish();
                    //DatabaseLoader.process_request()

                }
            }
        });

        deliverInOneHour.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    deliverInOneHour.setChecked(true);
                    return true;
                }
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    deliverInOneHour.setChecked(false);
                    return false;
                }

                if (!isDeliveryInOneHour) {

                    deliverInOneHour.setChecked(true);
                    setOtherTime.setChecked(false);
                    isDeliveryInOneHour = true;
                }
                return true;
            }

        });
        setOtherTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN) {
                    setOtherTime.setChecked(true);
                    return true;
                }
                if (event.getAction()!=MotionEvent.ACTION_UP) {
                    setOtherTime.setChecked(false);
                    return false;
                }
                ((CheckoutActivity) getActivity()).showCustomTimeDialog();
                if (isDeliveryInOneHour) {
                    deliverInOneHour.setChecked(false);
                    setOtherTime.setChecked(true);
                    isDeliveryInOneHour = false;
                }
                return true;
            }

        });
        deliverInOneHour.setChecked(true);

//        rootView.findViewById(R.id.submitOrder).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String phoneNumber = phoneInput.getText().toString();
//                String deliveryTime = null;
//
//                List<NameValuePair> params = new ArrayList<NameValuePair>(2);
//                params.add(new BasicNameValuePair("user_id", User.userId));
//                params.add(new BasicNameValuePair("address_street", streetInput.getText().toString()));
//                params.add(new BasicNameValuePair("address_home", houseInput.getText().toString()));
//                params.add(new BasicNameValuePair("address_flat", flat.getText().toString()));
//                params.add(new BasicNameValuePair("address_floor", flat.getText().toString().charAt(0)+""));
//
//                try {
//                    String addressId = new PostAddress().execute(params).get();
//                    ((CheckoutActivity) getActivity()).postOrder(addressId, phoneNumber, isDeliveryInOneHour);
//                } catch (Exception e) {
//                    Log.e(e.toString(), "Post request");
//                }
//            }
//        });
    }

    public static class PostAddress extends AsyncTask<List<NameValuePair>, Void, String> {
        boolean isNetworkExist = true;


        protected void onPreExecute() {

        }

        protected String doInBackground(List<NameValuePair>... params) {
            String addressId = null;
            try {
                addressId = processPostRequest(POST_ADDRESS_URL, params[0]);
            } catch (NoNetworkException e) {
                Log.e(e.toString(), "NoNetworkE");
                isNetworkExist = false;
            }
            return addressId;
        }


        protected void onPostExecute(String addressId) {

        }

        private String processPostRequest(String urlString, List<NameValuePair> params) throws NoNetworkException{
            String result = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(urlString);

            httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
//            Header setUtf8header = new BasicHeader("Content-Type", "charset=utf-8");

            try {
                // Add your data
                httppost.setEntity(new UrlEncodedFormEntity(params));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    InputStream inputStream = response.getEntity().getContent();
                    result = convertStreamToString(inputStream);
                } else {
                    //Closes the connection.
                    Log.e(response.getStatusLine().getStatusCode()+"", "Bad request ");
                }
            } catch (Exception e) {
                Log.e(e.toString(), "Some exception "+ POST_ADDRESS_URL);
                throw new NoNetworkException();
            }
            return result;
        }
        private String convertStreamToString(java.io.InputStream is) {
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
    }


    static {
        streets = new String[] {
                "40-летия Октября пр-т.",
                "Автозаводская",
                "Авторемонтная",
                "Агитаторская",
                "Азербаджанская",
                "Азовская",
                "Аистова",
                "академика Бутлерова",
                "Академика Шлихтера",
                "Алексеевская",
                "Алишера Навои",
                "Алма-Атинская",
                "Амосова Николая",
                "Амурская пл.",
                "Амурская ул.",
                "Анищенко",
                "Антонова Авиаконструктора",
                "Армейская",
                "Армянская",
                "Арсенальная площадь",
                "Арсенальная ул.",
                "Арсенальный пер.",
                "Архитекторская",
                "Аскольдов пер.",
                "Астраханская",
                "Ахматовой Анны",
                "Аэродромная",
                "Бажана пр-т",
                "Бажова",
                "Байкальская",
                "Байковая",
                "Бакунина",
                "Балакирева",
                "Балакирева пер.",
                "Бальзака",
                "Банковая",
                "Барбюса Анри",
                "Баррикадная",
                "Бассейная",
                "Бастионная",
                "Бастионный пер.",
                "Батумская",
                "Батуринский переулок",
                "Белицкого Академика",
                "Белогородская",
                "Белокур Екатерины",
                "Беломорская",
                "Березнева",
                "Березняковская",
                "Беренбойма",
                "Бессарабская площадь",
                "Блакитного",
                "Богдановская",
                "Богомольца Академика",
                "Богунская",
                "Богунский пер.",
                "Боевая",
                "Боженка",
                "Боженко",
                "Бойченко Александра",
                "Большая Китаевская",
                "Бориславская",
                "Бориспольская",
                "Боровиченко Марии",
                "Бортницкая",
                "Борщаговская",
                "Бошков Яр",
                "Братиславская",
                "Броварской пр-т",
                "Бродовская",
                "Бродовский пер.",
                "Брюллова",
                "Брянская",
                "Бубнова Андрея",
                "Будищанская",
                "Буйко Профессора",
                "Буковинская",
                "бул. Давыдова",
                "бул.Перова",
                "Бульвар Перова от  номера",
                "бульвар труда",
                "Бурмистенко пер.",
                "Бурмистенко ул.",
                "Бусловская",
                "Бутлерова академика",
                "Бучмы Амвросия",
                "Быкова",
                "Бышевский пер.",
                "Вакуленчука",
                "Василевская",
                "Василенко Николая",
                "Васильковская",
                "Васильковский пер.",
                "Васильченко",
                "Васнецова",
                "Ватутина",
                "Ватутина (Соломенский р-н)",
                "Ватутина генерала пр-ст",
                "Вербицгого архитектора",
                "Вересневая",
                "Верхнегорная",
                "Верхний пер.",
                "Верхняя",
                "Верховной Рады буль.",
                "Вершигоры",
                "Весенняя",
                "Вигуровский б-р",
                "Викентия Беретти",
                "Вильде Эдуарда",
                "Вильнюсская",
                "Вильямса Академика",
                "Винницкая",
                "Виноградный пер.",
                "Вирменская",
                "Вискозная",
                "Витавская",
                "Витянская",
                "Вишни Остапа",
                "Вишняковская",
                "Владимиро-Лыбедская",
                "Владимирская",
                "Владимирский спуск",
                "Внешняя",
                "Водогонная",
                "Военная",
                "Военный проезд",
                "Воздухофлотская ул.",
                "Воздухофлотский пр-т.",
                "Вокзальная пл.",
                "Вокзальная ул.",
                "Волга-Донская",
                "Волгоградская",
                "Волгодонский переулок",
                "Волжская",
                "Волжский пер.",
                "Волкова",
                "Волынская",
                "Воскресенская",
                "Воссоединения пр.",
                "Воссоединения пр-т",
                "Вузовская",
                "Выборгская",
                "Выдубицкая",
                "Высоцкого",
                "Гаврилюка",
                "Гагарина пр-т",
                "Гаевая",
                "Газовая",
                "Гайдара",
                "Гайсинская",
                "Гайцана Николая",
                "Галяна Ярослава",
                "Гарина Бориса",
                "Гарматная",
                "Гастелло",
                "Гатная",
                "Гашека",
                "Гвардейская",
                "Героев Великой Отечественной пл.",
                "Героев Войны",
                "Героев Крут аллея",
                "Героев Обороны",
                "Героев Севастополя",
                "Гетьмана Вадима",
                "Глазунова",
                "Глинки",
                "Глушкова Академика",
                "Гмыри Бориса",
                "Говорова Маршала пер.",
                "Говорова Маршала ул.",
                "Гоголя",
                "Гоголя (Соломенский р-н.)",
                "Головко Андрея",
                "Голосеевская пл.",
                "Голосеевская ул.",
                "Голосеевский пер.",
                "Голосеевский пр-т.",
                "Горвица Александра",
                "Гордиенко Костя пер.",
                "Гористая",
                "Гористый пер.",
                "Горловская",
                "Городецкого Архитектора",
                "Городня",
                "Горького",
                "Горького (Голосеевский р-н.)",
                "Горького пер.",
                "Госпитальная",
                "Госпитальный пер.",
                "Гостинная",
                "Грабовского Павла пер.",
                "Грабовского Павла ул.",
                "Градинская",
                "Гребенки",
                "Григоренко",
                "Григоренко Петра",
                "Гринченко Николая",
                "Гришка",
                "Гродненская",
                "Гродненский переулок",
                "Громадская",
                "Громовой Ульяны",
                "Грузинская",
                "Грушевского Михаила",
                "Гулака-Артемовского пер.",
                "Гусовского",
                "Гучный пер.",
                "Далекая",
                "Данкевича",
                "Дарвина",
                "Дарницкий б-р",
                "Дашавская",
                "Двинская",
                "Дежнева пер.",
                "Дежнева ул.",
                "Декабристов",
                "Деревоотделочная ул.",
                "Деревоотделочный пер.",
                "Деснянская",
                "Деснянская (Соломенский р-н.)",
                "Дзержинского",
                "Дзержинского пл. (Печерский р-н.)",
                "Дибровная",
                "Дибровный пер.",
                "Димеевская",
                "Димитрова",
                "Димитрова (Печерский р-н.)",
                "Днепровская наб",
                "Днепровский переулок",
                "Днепровский спуск",
                "Днепродзержинская",
                "Днепропетровская",
                "Днепропетровское шоссе",
                "Днестровская",
                "Добролюбова пер.",
                "Добролюбова ул.",
                "Добрососедская ул.",
                "Добрососедский пер.",
                "Добрый Путь",
                "Доватора Генерала пер.",
                "Доватора Генерала ул.",
                "Довбуша",
                "Довженка",
                "Докучаевская ул.",
                "Докучаевский пер.",
                "Докучаевский тупик",
                "Долинная",
                "Донецкая",
                "Донецкий пер.",
                "Донская",
                "Донский пер.",
                "Донца Михаила",
                "Драгоманова",
                "Драгомирова",
                "Драйзера",
                "Дружбы Народов бул.",
                "Дружная",
                "Дубенская",
                "Дубинина Володи",
                "Дубового Ивана",
                "Дуки Степана",
                "Европейская площадь",
                "Енисейская",
                "Ереванская",
                "Жилянская",
                "Жмаченко",
                "Жукова",
                "Жуковского Василия пер.",
                "Жуковского Василия ул.",
                "Жулянская",
                "Забилы Виктора",
                "Заболотного Академика",
                "Завальная",
                "Заднепровского Михаила",
                "Задорожный пер.",
                "Закарпатская",
                "Закревского",
                "Залесная",
                "Зализнычная",
                "Зализнычное шоссе",
                "Заломова Петра",
                "Залужная",
                "Залужный пер.",
                "Замывная",
                "Заньковецкой",
                "Западная",
                "Западный пер.",
                "Запарожца Петра",
                "Заповетная",
                "Заповитный пер.",
                "Заречная",
                "Заслонова Константина",
                "Затевахина Полковника",
                "Затишная",
                "Зверинецкая",
                "Зверинецкий пер.",
                "Згуровская",
                "Здолбуновская",
                "Зеленая",
                "Зеленогорская",
                "Землянская",
                "Землянский пер.",
                "Зенитная",
                "Златопольская",
                "Знаменская",
                "Золотоношская",
                "Ивана Дубового",
                "Ивана Сергиенка",
                "Иванова Андрея",
                "Изюмская",
                "Ильича",
                "Ингульский пер.",
                "Индустриальный пер.",
                "Инженерная",
                "Инженерный пер.",
                "Институтская",
                "Иртышская",
                "Искровская",
                "Каблукова Академика",
                "Кавказская",
                "Кадетский Гай",
                "Кайсарова",
                "Калачевская",
                "Каменева Командарма",
                "Каменяров пер.",
                "Каменяров ул.",
                "Камышинская",
                "Канальная",
                "Караваева Профессора",
                "Карбышева Генерала",
                "Карельский пер.",
                "Карла Маркса",
                "Карла Маркса (Соломенский р-н)",
                "Кармелюка ул",
                "Кармелюка Устина пер-к",
                "Карпатская",
                "Карпинского академика",
                "Каръерная",
                "Касияна Василия",
                "Каунаская",
                "Каховская",
                "Качалова",
                "Каштановая",
                "Кащенко Академика",
                "Квитки-Основьяненко пер.",
                "Квитки-Основьяненко ул.",
                "Керамический пер.",
                "Керченская",
                "Кибальчича Николая",
                "Киевская",
                "Киквидзе",
                "Киото",
                "Кирова",
                "Кировоградская",
                "Кировоградский пер.",
                "Кирпичный пер.",
                "Китаевская",
                "Кишиневская",
                "Клеманская",
                "Клименко Ивана",
                "Клиническая ул.",
                "Клинический пер.",
                "Клиновый пер.",
                "Кловский спуск",
                "Княжий затон",
                "Кобылянской Ольги пер.",
                "Кобылянской Ольги ул.",
                "Ковалевской Софии",
                "Ковальский пер.",
                "Ковельская",
                "Ковпака",
                "Козацкая",
                "Козацкий пер.",
                "Козилецкая",
                "Козицкого Филиппа",
                "Козловского Ивана пер.",
                "Козятинская",
                "Козятинский пер.",
                "Коллективизации",
                "Коломиевский пер.",
                "Колосковая",
                "Кольцевая дорога",
                "Комарова Космонавта пр-т.",
                "Комбайнеров",
                "Комодемьянской Зои",
                "Комсомольская",
                "Комунальная",
                "Кондукторская",
                "Конева",
                "Конечная",
                "Конотопская",
                "Конотопский пер.",
                "Кооперативная",
                "Коренная",
                "Короленковская",
                "Корсунь-Шевченковская",
                "Корчеватская",
                "Корчеватский пер.",
                "Космическая",
                "Космонавтов пл.",
                "Костычева Академика",
                "Котляревского",
                "Кочерги Ивана",
                "Кочубеевская",
                "Кочубеевский пер.",
                "Кошевого Маршала",
                "Кошевого Олега",
                "Кошевого Олега пер.",
                "Кошица",
                "Крайняя",
                "Краковская",
                "Краматорская",
                "Краматорский пер.",
                "Красикова Петра",
                "Красиловская",
                "Красная",
                "Красноармейская",
                "Красноармейский пер.",
                "Красногвардейская",
                "Красногвардейский пер.",
                "Краснодонская ул.",
                "Краснодонский пер.",
                "Краснозвездный пр-т.",
                "Краснознаменная",
                "Краснознаменный пер.",
                "Краснокуцкая",
                "Краснопартизанская",
                "Красноткацкая",
                "Красный переулок",
                "Крейсера Аврора",
                "Крепостной пер.",
                "Крещатик",
                "Кривоноса Максима пер.",
                "Кривоноса Максима ул.",
                "Кривоноса Петра пл.",
                "Криворожская",
                "Криничная",
                "Кропивницкого",
                "Круглая",
                "Круглоуниверситетская",
                "Крупской",
                "Крутая",
                "Крутогорная",
                "Крутой спуск",
                "Крушельницкой Соломии",
                "Крымская",
                "Кубанская",
                "Кудри Ивана",
                "Кудряшова пер.",
                "Кудряшова ул.",
                "Кунанбаева Абая",
                "Купьянская ул.",
                "Купьянский пер.",
                "Кургановская",
                "Кургановский пер.",
                "Курнатовського",
                "Курская",
                "Курчатов",
                "Кустанайская ул.",
                "Кустанайский пер.",
                "Кутузова",
                "Кутузова (Печерский р-н.)",
                "Кутузова пер. (Печерский р-н.)",
                "Лабораторная",
                "Лабораторный пер.",
                "Лаврская",
                "Лаврский пер.",
                "Лаврухина",
                "Лазурная",
                "Ластовского",
                "Лауреатская",
                "Лебедева Академика",
                "Лебедева николая",
                "Лебедева-Кумача",
                "Леваневского",
                "Левитана",
                "Лейпцигская",
                "Ленина",
                "Ленина (Соломенский р-н)",
                "Ленинградская пл.",
                "Лепсе Ивана бул.",
                "Лермонтова",
                "Леси Украинки бул.",
                "Леси Украинки площадь",
                "Лескова",
                "Лесковская",
                "Лесничая",
                "Лесной пр-т",
                "Лесоводная",
                "Летняя",
                "Линейная",
                "Липская",
                "Липский пер.",
                "Лисичанская ул.",
                "Лисичанский пер.",
                "Листопадная",
                "Листопадный пер.",
                "Литовский пер.",
                "Лихачева бул.",
                "Лобачевского",
                "Лобачевского пер-к",
                "Локомативная",
                "Ломоносова",
                "Лубенская",
                "Луганская",
                "Луговая",
                "Лукашевича Николая",
                "Лумумбы Патриса",
                "Луначарского",
                "Лыбедская",
                "Лыбедская площадь",
                "Лысогорская",
                "Лысогорский спуск",
                "Любомирская",
                "Люборская",
                "Любченко Панаса",
                "Лютеранская",
                "Лютневая",
                "Лятошинского",
                "Льва Толстого",
                "Льва Тостого площадь",
                "Магнитогорская",
                "Магнитогорский пер.",
                "Мазепы Ивана",
                "Майкопская",
                "Майкопский пер.",
                "Майская",
                "Майский пер.",
                "Макаренка",
                "Малодогвардейская",
                "Малоземельная",
                "Малокитаевская",
                "Малокитаевский пер.",
                "Малышко",
                "Марины Расковой",
                "Мартиросяна",
                "Маршалская",
                "Марьяненко Ивана",
                "Матеюка",
                "Матросова Александра",
                "Матыкина Генерала",
                "Машиностроительная ул.",
                "Машиностроительный пер.",
                "Маяковского пр-т",
                "Маяковского ул",
                "Медвинская",
                "Медицинская",
                "Медовая",
                "Мейтуса Композитора",
                "Мельчакова",
                "Менделеева",
                "Металлистов пер.",
                "Металлистов ул.",
                "Метрологическая",
                "Метростроевская",
                "Механизаторов",
                "Мечникова",
                "Микитенка Ивана",
                "Милославская",
                "Милютенко",
                "Минина",
                "Мира пр-т",
                "Мирного Панаса",
                "Мирного Панаса пер.",
                "Мироновская",
                "Миропольская",
                "Миру",
                "Мицкевича Адама",
                "Мичурина",
                "Мичурина пер.",
                "Мишина Михаила",
                "Мишуги",
                "Можайская",
                "Монтажников",
                "Москвина",
                "Москвина пер.",
                "Московская",
                "Московская площадь",
                "Московский пер.",
                "Мостищенская",
                "Мостова",
                "Мостовой пер.",
                "Моторная",
                "Моторный пер.",
                "Музейный пер.",
                "Мурманская",
                "Мыстецкая",
                "Набережная",
                "Набережное шоссе",
                "Набережно-Жилянская",
                "Набережно-Корчеватская",
                "Набережно-Печерская дорога",
                "Набережный пер.",
                "Надднепрянское шоссе",
                "Надъярная",
                "Наклонная",
                "Народная",
                "Народного Ополчения",
                "Народный пер.",
                "Науки пр-т.",
                "Нежинская",
                "Некрасова",
                "Неманская",
                "Немировича-Данченка",
                "Неходы Ивана",
                "Нечуя-Левицкого",
                "Нижнеключевая",
                "Николаева",
                "Никольско-Ботаническая",
                "Никольско-Слободская",
                "Никопольская",
                "Нищинского Петра",
                "Новаторов",
                "Новгородская",
                "Нововокзальная",
                "Новодарницкая",
                "Новодачный пер.",
                "Новокорчеватская",
                "Новомичуренская",
                "Новонаводницкий пер.",
                "Новонародный пер.",
                "Новопечерский пер.",
                "Новопироговская",
                "Новополевая",
                "Новоселицкая",
                "Новоселицкий пер.",
                "Новоторов переулок",
                "Оборонная ул.",
                "Оборонный пер.",
                "Овражный пер.",
                "Одесская",
                "Одесская пл.",
                "Озерная",
                "Олейника Степана",
                "Ольгинская",
                "Ольшанская",
                "Ольшанский пер.",
                "Орбитная",
                "Организаторская",
                "Орлыка Филиппа",
                "Оросительная",
                "Освободителей пр-т",
                "Оскольская",
                "Оскольский пер.",
                "Островная",
                "Островского Николая пер.",
                "Островского Николая ул.",
                "Отрадный пр-т.",
                "Охотская",
                "Охотский пер.",
                "Охтырская",
                "Охтырский пер.",
                "Очаковская ул.",
                "Очаковский пер.",
                "Павла Усенка",
                "Панельная",
                "Панорамная",
                "Панфиловцев пер.",
                "Панфиловцев ул.",
                "Паньковская",
                "Парковая дорога",
                "Парниковая",
                "Патриотов",
                "Паустовского",
                "пер. Строителей",
                "Первозванного Андрея пл.",
                "Первомайского Леонида",
                "Передовая",
                "Перспективная",
                "Петровская аллея",
                "Петровского",
                "Петровского (Соломенский р-н.)",
                "Петрозаводская",
                "Петрусенко Оксаны",
                "Печерская  пл.",
                "Печерский спуск",
                "Пешеходный пер.",
                "Пироговского Александра",
                "Пирятинская",
                "Пирятинский пер.",
                "Писаржевского Академика",
                "Питерская",
                "Планерная",
                "Планетная",
                "Платоновская",
                "Платоновский пер.",
                "Плеханова",
                "Плещеева",
                "Плещеева пер.",
                "Победы пр-т.",
                "Погребы Каштановая",
                "Погребы Киевская",
                "Погребы Коцюбинского",
                "Погребы Тепличная",
                "Подборная",
                "Подборный пер.",
                "Подвысоцкого Профессора",
                "Поддубного Ивана",
                "пожарского",
                "Полевая ул.",
                "Полевой пер.",
                "Полесская ул",
                "Полесский переулок",
                "Ползунова",
                "Политехническая ул.",
                "Политехнический пер.",
                "Полоцкий",
                "Польова",
                "Попильнянская",
                "Попудренка",
                "Пост-Волынская",
                "Постовая",
                "Потебни Академика",
                "Потехина Полковника",
                "Правобережная",
                "Пражская",
                "Предславинская",
                "Пржевальского",
                "Приветная",
                "Привокзальная",
                "Приколейная",
                "Причальная",
                "Проводницкая",
                "Пролетарская",
                "Пролетарский пер.",
                "Промышленная",
                "Просвещения",
                "Протасов Яр спуск",
                "Протасов Яр ул.",
                "Профинтерна",
                "Профинтерна пер.",
                "Профсоюзная",
                "Псковская",
                "Псковский пер.",
                "Пулюя Ивана",
                "Пуховская",
                "Пушкинская",
                "Пчёлки Елены",
                "Пятигорская",
                "Пятигорский пер.",
                "Рабочая",
                "Радиальная",
                "Радищева пер.",
                "Радищева ул.",
                "Радостная",
                "Радужная",
                "Радунская",
                "Радченко Петра",
                "Радянская",
                "Радянский пер.",
                "Раевского Николая",
                "Раздельная",
                "Раисы Окипной",
                "Райгородская",
                "Райгородский пер.",
                "Ракетная",
                "Ревуцкого",
                "Регенераторная",
                "Редутная",
                "Редутный пер.",
                "Резницкая",
                "Ремесленный пер.",
                "Ремонтная",
                "Реута Михаила пер.",
                "Ржевский",
                "Ржищевская",
                "Римского-Корсакова",
                "Рогнединская",
                "Рогозовская",
                "Родимцева Генерала",
                "Ромодановская",
                "Российская",
                "Россошанская",
                "Рощанский пер.",
                "Руденко Ларисы",
                "Руднева переулок",
                "Руднева ул",
                "Русановская набережная",
                "Русановский буль",
                "Рыбальская",
                "Рыбная ул.",
                "Рыбный пер.",
                "Рыболовецкая",
                "Рыльского Максима",
                "Сабурова",
                "Саврасова",
                "Садова",
                "Садовая",
                "Садовая (Печерский р-н.)",
                "Садовая ул. (Соломенский р-н.)",
                "Садовый пер. (Соломенский р-н.)",
                "Саксаганского",
                "Салтыкова-Щедрина",
                "Самборский пер.",
                "Санаторная",
                "Санитарная",
                "Саперное Поле",
                "Саперно-Слободская",
                "Саперно-Слободской проезд",
                "Сафроновский пер.",
                "Светлогорская",
                "Святославская",
                "Севастопольская",
                "Севастопольская площадь",
                "Седова",
                "Седовцев пер.",
                "Седовцов",
                "Селекционеров",
                "Сельськохозяйственный пер.",
                "Семеновская",
                "Серафимовича",
                "Сергиенко Ивана",
                "Серова Валентина",
                "Серпуховский пер.",
                "Сеченова",
                "Сивашская",
                "Сигнальная",
                "Симоненка",
                "Симферопольская",
                "Сирко Ивана",
                "Сквирская",
                "Сквирский пер.",
                "Скоропадского Павла",
                "Скрипника Николая",
                "Славгородская",
                "Славгородский пер-к",
                "Славы площадь",
                "Славянская",
                "Словечанская",
                "Смелянская",
                "Смоленская",
                "Смолича Юрия",
                "Смольная",
                "Снайперская",
                "Снижнянский пер.",
                "Сновская",
                "Совская",
                "Соловцова Николая",
                "Соловьиная",
                "Соломенская пл.",
                "Соломенская ул.",
                "Сормовская",
                "Сорочинская",
                "Сортувальная",
                "Сосницкая",
                "Сосюры",
                "Социалистическая",
                "Спивака Михаила",
                "Спокойная",
                "Спортивная площадь",
                "Срибнокильская",
                "Ставковая",
                "Стадионная ул.",
                "Стадионная-",
                "Стадионный пер.",
                "Стальського Сулеймана",
                "Станиславского",
                "Старонаводницкая",
                "Старосельськая",
                "Стельмаха Михаила",
                "Степового Якова",
                "Столетова",
                "Столетова пер.",
                "Столичное шоссе",
                "Стражеско Академика",
                "Стратегический пер.",
                "Стратегическое шоссе",
                "Стрелковая",
                "Строителей",
                "Стройиндустрии",
                "Струтинского Сергея",
                "Суворова",
                "Суздальская",
                "Сумская",
                "Сурикова Василия",
                "Тальновская",
                "Тампере",
                "Танашпольская",
                "Танкистов",
                "Тарасовская",
                "Ташкентская",
                "Тверская",
                "Тверской тупик",
                "Телеграфный пер.",
                "Тельмана",
                "Тепловозная",
                "Теремковская",
                "Теремковский пер.",
                "Теслярская",
                "Тимирязевская",
                "Тимирязевский пер.",
                "Тихая",
                "Тихвинский пер.",
                "Тихоновский пер.",
                "Тихорецкая",
                "Тихорецкий пер.",
                "Тобольский пер.",
                "Товарная",
                "Тополевая",
                "Траншейная",
                "Трахтемировская",
                "Тростинецкая",
                "Труда б-р",
                "Трутенко Онуфрия",
                "Тульский пер.",
                "Туманяна     Рус-ка",
                "Тупикова Генерала",
                "Тутковского Академика",
                "Тычины пр-т",
                "Ужгородская",
                "Ужгородский пер.",
                "Уманская",
                "Университетская",
                "Уральская",
                "Уральский пер.",
                "Урицкого",
                "Урловская",
                "Усенко Павла",
                "Устимовский пер.",
                "Учебная",
                "Учебный пер.",
                "Учительская",
                "Ушакова Адмирала",
                "Ушинского",
                "Фанерный пер.",
                "Фастовская",
                "Федорова Ивана",
                "Федосеева",
                "Федьковича пер.",
                "Федьковича ул.",
                "Феодосийская",
                "Феодосийский пер.",
                "Фестивальная",
                "Фиалека Ивана",
                "Фиалковая",
                "Физкультурная пл.",
                "Физкультуры пер.",
                "Физкультуры ул.",
                "Филатова Академика",
                "Филатова Академика пер.",
                "Флоренции",
                "Франко Ивана",
                "Фрометовская",
                "Фрометовский спуск",
                "Фурманова",
                "Фучика Юлиуса",
                "Харьковский переулок",
                "Харьковское ш-се",
                "Холмогорская",
                "Холмогорский пер.",
                "Хоральская",
                "Хорольский переулок",
                "Хортицкая",
                "Хортицкий пер.",
                "Хотовская",
                "Хуторская",
                "Хуторской пер.",
                "Хутояровский пер.",
                "Царика Григория",
                "Цветаевой",
                "Цветущая",
                "Целинная",
                "Цимбалов Яр",
                "Цимбалов Яр пер.",
                "Циолковского пер.",
                "Циолковского ул.",
                "Цитадельная",
                "Чабановская",
                "Чавдар Елизавета",
                "Чайкиной Лизы",
                "Чапаевское шоссе",
                "Челябинская",
                "Черемшины МАрка",
                "Черешневый пер.",
                "Чернивецкая",
                "Черниговская",
                "Черниговский переулок",
                "Черногорская",
                "Чернышевского",
                "Чешская",
                "Чигорина",
                "Чоколовский бульвар",
                "Чугуевский переулок",
                "Чудновского",
                "Чумака Василия",
                "Шалетт город",
                "Шевченка",
                "Шевченка пер.",
                "Шевченко Тараса бул.",
                "Шелковичная",
                "Шепелева Николая",
                "Широкая",
                "Школьная",
                "Шовкуненко Алексея",
                "Шолом Алейхемам",
                "Шота Руставели",
                "Шпилевая",
                "Шумского Юрия",
                "Щепкина",
                "Щоголовский пер.",
                "Щорса",
                "Щорса (Печерский р-н.)",
                "Щорса пер. (Печерский р-н.)",
                "Электротехническая",
                "Энергетиков пер.",
                "Энергетиков ул.",
                "Энтузиастов",
                "Эренбурга Ильи",
                "Эрнста Федора",
                "Эспланадная",
                "Юлаева Салавата",
                "Юность",
                "Юрия Гагарина пр-т",
                "Яблоневая ул.",
                "Яблоневый пер.",
                "Яблонской Татьяны",
                "Ягодная",
                "Ягодный пер.",
                "Яготинская",
                "Якубовского Маршала",
                "Ялтинская",
                "Ялынковая",
                "Ямская",
                "Яна Василия",
                "Яна Райнеса",
                "Январский пер.",
                "Январского Восстания",
                "Янгеля Академика",
                "Янки Купалы",
                "Ясеневый пер.",
                "Ясиневатский пер.",
                "Яслинская",
                "Ясная",
                "Ясный пер.",


        };
    }


}
