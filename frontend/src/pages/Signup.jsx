import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import styles from './Auth.module.css';

// 회원가입 페이지 컴포넌트
export default function Signup() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [form, setForm] = useState({ name: '', email: '', password: '' });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setErrors({ ...errors, [e.target.name]: '' });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors({});
    setLoading(true);
    try {
      const res = await api.post('/auth/signup', form);
      const { token, email, name } = res.data.data;
      login(token, { email, name });
      navigate('/dashboard');
    } catch (err) {
      const data = err.response?.data;
      if (data?.data && typeof data.data === 'object') {
        // 필드별 유효성 검사 오류
        setErrors(data.data);
      } else {
        setErrors({ general: data?.message || '회원가입에 실패했습니다' });
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <div className={styles.logo}>🏦</div>
        <h1 className={styles.title}>Bank Simulator</h1>
        <h2 className={styles.subtitle}>회원가입</h2>

        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.field}>
            <label className={styles.label}>이름</label>
            <input
              className={`${styles.input} ${errors.name ? styles.inputError : ''}`}
              type="text"
              name="name"
              value={form.name}
              onChange={handleChange}
              placeholder="이름을 입력하세요"
              required
            />
            {errors.name && <span className={styles.fieldError}>{errors.name}</span>}
          </div>

          <div className={styles.field}>
            <label className={styles.label}>이메일</label>
            <input
              className={`${styles.input} ${errors.email ? styles.inputError : ''}`}
              type="email"
              name="email"
              value={form.email}
              onChange={handleChange}
              placeholder="이메일을 입력하세요"
              required
            />
            {errors.email && <span className={styles.fieldError}>{errors.email}</span>}
          </div>

          <div className={styles.field}>
            <label className={styles.label}>비밀번호</label>
            <input
              className={`${styles.input} ${errors.password ? styles.inputError : ''}`}
              type="password"
              name="password"
              value={form.password}
              onChange={handleChange}
              placeholder="비밀번호 (최소 6자)"
              required
            />
            {errors.password && <span className={styles.fieldError}>{errors.password}</span>}
          </div>

          {errors.general && <p className={styles.error}>{errors.general}</p>}

          <button className={styles.button} type="submit" disabled={loading}>
            {loading ? '처리 중...' : '회원가입'}
          </button>
        </form>

        <p className={styles.link}>
          이미 계정이 있으신가요? <Link to="/login">로그인</Link>
        </p>
      </div>
    </div>
  );
}
