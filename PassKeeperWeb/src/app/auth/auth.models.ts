export interface LoginCredentials {
    email: string;
    password: string;
}
export interface SingInData {
    email: string;
    password: string;
}
export interface User{
    email: string;
    password: string;
    is_premium: boolean;
    is_staff: boolean;

}
export interface LoginResponse{
    user?:User
    access?:string;
    refresh?:string;
    message:string;

}

export interface JwtTokens{
    access:string;
    refresh:string;
}