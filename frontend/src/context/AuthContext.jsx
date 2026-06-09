import React, { createContext, useContext, useState } from 'react';

// 인증 상태를 전역으로 관리하는 Context
const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  // 로컬 스토리지에서 초기 사용자 정보 로드
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('user');
    return stored ? JSON.parse(stored) : null;
  });

  // 로그인 성공 시 호출 - 토큰과 사용자 정보 저장
  const login = (token, userInfo) => {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(userInfo));
    setUser(userInfo);
  };

  // 로그아웃 - 로컬 스토리지 초기화
  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

// 커스텀 훅 - 컴포넌트에서 쉽게 인증 상태 접근
export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth는 AuthProvider 안에서 사용해야 합니다');
  }
  return context;
}
