package com.keiskeismartsystem.model;

/**
 * Created by zeta on 10/13/2015.
 */
public class User {
    public static final String KEY = "USER_DATA";
    private int _id, _total_bought;
    private String _code, _name, _username,_email, _password, _telephone, _description, _photo, _gcm_id, _address, _pinbb, _path_user_int, _fb, _instagram, _website, _bonus;
    private char _gender;
    private boolean _enabled, _filled;

    public User(){

    }
    public User(String code, String name, String username, String email, String password, String telephone, char gender, String description, String photo, String pinbb, boolean enabled){
        this._code= code;
        this._username = username;
        this._email = email;
        this._password = password;
        this._enabled = enabled;
        this._description = description;
        this._name = name;
        this._gender = gender;
        this._telephone = telephone;
        this._photo = photo;
        this._pinbb = pinbb;

    }
    public void setID(int id){
        this._id = id;
    }
    public  int getID()
    {
        return  this._id;
    }
    public void setUsername(String username){
        this._username = username;
    }
    public  String getUsername(){
        if(this._username == null)
            return "";
        return  this._username;
    }
    public void setEmail(String email){
        this._email = email;
    }
    public String getEmail(){
        if(this._email == null)
            return "";
        return this._email;
    }
    public void setPassword(String password){
        this._password = password;
    }
    public String getPassword(){
        if(this._password == null)
            return "";
        return  this._password;
    }
    public void setEnabled(boolean enabled){
        this._enabled = enabled;
    }
    public boolean getEnabled(){
        return this._enabled;
    }
    public void setFillled(boolean filled){
        this._filled = filled;
    }
    public boolean getFilled(){
        return this._filled;
    }

    public void setName(String name){
        this._name= name;
    }
    public String getName()
    {
        if(this._name == null)
            return "";
        return this._name;
    }
    public void setDescription(String description){
        this._description= description;
    }
    public String getDescription(){
        return this._description;
    }
    public void setTelephone(String telephone){
        this._telephone= telephone;
    }
    public String getTelephone(){
        if(this._telephone == null)
            return "";
        return this._telephone;
    }
    public void setGender(char gender){
        this._gender= gender;
    }
    public char getGender(){
        return this._gender;
    }
    public void setPhoto(String photo){
        this._photo = photo;
    }
    public String getPhoto(){
        if(this._photo == null)
            return "";
        return this._photo;
    }
    public void setGCMID(String gcm_id){
        this._gcm_id = gcm_id;
    }
    public  String getGCMID()
    {
        return  this._gcm_id;
    }
    public void setAddress(String address){
        this._address = address;
    }
    public  String getAddress()
    {
        if(this._address == null)
            return "";
        return  this._address;
    }
    public void setPinBB(String pinBB){
        this._pinbb = pinBB;
    }
    public String getPinBB()
    {
        if(this._pinbb == null)
            return "";
        return  this._pinbb;
    }
    public void setTotalBought(int total){
        this._total_bought = total;
    }
    public int getTotalBought()
    {
        return  this._total_bought;
    }
    public void setPathUserInt(String path){
        this._path_user_int = path;
    }
    public String getPathUserInt()
    {
        if(this._path_user_int== null)
            return "";
        return  this._path_user_int;
    }
    public void setCode(String code){
        this._code = code;
    }
    public String getCode()
    {
        return  this._code;
    }
    public void setFB(String fb){
        this._fb = fb;
    }
    public String getFB()
    {
        return this._fb;
    }
    public void setWebsite(String website){
        this._website = website;
    }
    public String getWebsite()
    {
        return this._website;
    }
    public void setInstagram(String ig){
        this._instagram = ig;
    }
    public String getInstagram()
    {
        return this._instagram;
    }
    public void setBonus(String bonus){
        this._bonus = bonus;
    }
    public String getBonus()
    {
        return this._bonus;
    }
}

