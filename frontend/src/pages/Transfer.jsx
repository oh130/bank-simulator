import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../api/axios';
import styles from './Transfer.module.css';

// 송금 페이지 - 계좌 간 송금 처리
export default function Transfer() {
  const navigate = useNavigate();
  const [accounts, setAccounts] = useState([]);
  const [form, setForm] = useState({
    fromAccountNumber: '',
    toAccountNumber: '',
    amount: '',
    description: '',
  });
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');

  // 내 계좌 목록 로드 (출금 계좌 선택용)
  useEffect(() => {
    api.get('/accounts').then((res) => {
      setAccounts(res.data.data);
      if (res.data.data.length > 0) {
        setForm((prev) => ({
          ...prev,
          fromAccountNumber: res.data.data[0].accountNumber,
        }));
      }
    });
  }, []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setError('');
    setSuccess('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');
    try {
      await api.post('/transactions/transfer', {
        ...form,
        amount: parseFloat(form.amount),
      });
      setSuccess(`${form.amount}원 송금이 완료되었습니다!`);
      setForm((prev) => ({ ...prev, toAccountNumber: '', amount: '', description: '' }));
    } catch (err) {
      setError(err.response?.data?.message || '송금에 실패했습니다');
    } finally {
      setLoading(false);
    }
  };

  const formatBalance = (amount) =>
    new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW' }).format(amount);

  // 현재 선택된 출금 계좌의 잔액 표시
  const selectedAccount = accounts.find((a) => a.accountNumber === form.fromAccountNumber);

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <div className={styles.headerInner}>
          <Link to="/dashboard" className={styles.back}>← 대시보드</Link>
          <span className={styles.brand}>🏦 Bank Simulator</span>
        </div>
      </header>

      <main className={styles.main}>
        <div className={styles.card}>
          <h2 className={styles.title}>💸 송금하기</h2>

          <form onSubmit={handleSubmit} className={styles.form}>
            {/* 출금 계좌 선택 */}
            <div className={styles.field}>
              <label className={styles.label}>출금 계좌</label>
              {accounts.length === 0 ? (
                <p className={styles.noAccount}>계좌가 없습니다. 먼저 계좌를 개설하세요.</p>
              ) : (
                <select
                  className={styles.select}
                  name="fromAccountNumber"
                  value={form.fromAccountNumber}
                  onChange={handleChange}
                  required
                >
                  {accounts.map((acc) => (
                    <option key={acc.id} value={acc.accountNumber}>
                      {acc.accountNumber} ({formatBalance(acc.balance)})
                    </option>
                  ))}
                </select>
              )}
              {selectedAccount && (
                <span className={styles.balanceHint}>
                  사용 가능 잔액: {formatBalance(selectedAccount.balance)}
                </span>
              )}
            </div>

            {/* 수신 계좌번호 입력 */}
            <div className={styles.field}>
              <label className={styles.label}>수신 계좌번호</label>
              <input
                className={styles.input}
                type="text"
                name="toAccountNumber"
                value={form.toAccountNumber}
                onChange={handleChange}
                placeholder="예: 100-1234567"
                required
              />
            </div>

            {/* 송금 금액 */}
            <div className={styles.field}>
              <label className={styles.label}>송금 금액 (원)</label>
              <input
                className={styles.input}
                type="number"
                name="amount"
                value={form.amount}
                onChange={handleChange}
                placeholder="최소 1원 이상"
                min="1"
                required
              />
            </div>

            {/* 송금 메모 */}
            <div className={styles.field}>
              <label className={styles.label}>메모 (선택)</label>
              <input
                className={styles.input}
                type="text"
                name="description"
                value={form.description}
                onChange={handleChange}
                placeholder="송금 메모를 입력하세요"
                maxLength={50}
              />
            </div>

            {error && <p className={styles.error}>{error}</p>}
            {success && <p className={styles.success}>{success}</p>}

            <button
              className={styles.button}
              type="submit"
              disabled={loading || accounts.length === 0}
            >
              {loading ? '처리 중...' : '송금하기'}
            </button>
          </form>
        </div>
      </main>
    </div>
  );
}
