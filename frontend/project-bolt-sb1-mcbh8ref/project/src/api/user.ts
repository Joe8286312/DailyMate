import request from './request'

export interface User {
  id: number
  username: string
  email?: string
  avatar?: string
  isDelete: number
  createdAt: string
  updatedAt: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  email?: string
  avatar?: string
}

export interface UpdateProfileRequest {
  email?: string
  avatar?: string
  username?: string
}

export interface UpdatePasswordRequest {
  oldPassword: string
  newPassword: string
}

export const userApi = {
  login(data: LoginRequest) {
    return request.post<any, { success: boolean; token: string; user: User }>('/auth/login', data)
  },

  register(data: RegisterRequest) {
    return request.post<any, { success: boolean; msg: string; user: User }>('/auth/register', data)
  },

  getCurrentUser() {
    return request.get<any, User>('/user/me')
  },

  updateProfile(data: UpdateProfileRequest) {
    return request.put<any, { success: boolean; msg: string; user: User }>('/user/profile', data)
  },

  updatePassword(data: UpdatePasswordRequest) {
    return request.put<any, { success: boolean; msg: string }>('/user/password', data)
  },

  deleteAccount() {
    return request.delete<any, { success: boolean; msg: string }>('/user/me')
  },

  getUserList() {
    return request.get<any, User[]>('/user/list')
  }
}
