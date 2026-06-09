import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import styles from './Dashboard.module.css';

// 대시보드 - 내 계좌 목록을 보여주는 메인 페이지
export default function Dashboard() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [initialBalance, setInitialBalance] = useState('');
  const [creating, setCreating] = useState(false);
  const [error, setError] = useState('');

  // 페이지 진입 시 계좌 목록 로드
  useEffect(() => {
    fetchAccounts();
  }, []);

  const fetchAccounts = async () => {
    try {
      const res = await api.get('/accounts');
      setAccounts(res.data.data);
    } catch (err) {
      setError('계좌 목록을 불러오는 데 실패했습니다');
    } finally {
      setLoading(false);
    }
  };

  // 계좌 개설 처리
  const handleCreateAccount = async (e) => {
    e.preventDefault();
    setCreating(true);
    try {
      await api.post('/accounts', { initialBalance: parseFloat(initialBalance) });
      setShowModal(false);
      setInitialBalance('');
      fetchAccounts(); // 목록 새로고침
    } catch (err) {
      setError(err.response?.data?.message || '계좌 개설에 실패했습니다');
    } finally {
      setCreating(false);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  // 잔액 포맷 (원화 표시)
  const formatBalance = (amount) => {
    return new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW' }).format(amount);
  };

  return (
    <div className={styles.page}>
      {/* 상단 헤더 */}
      <header className={styles.header}>
        <div className={styles.headerInner}>
          <div className={styles.brand}>🏦 Bank Simulator</div>
          <div className={styles.userInfo}>
            <span>{user?.name}님 환영합니다</span>
            <button className={styles.logoutBtn} onClick={handleLogout}>로그아웃</button>
          </div>
        </div>
      </header>

      <main className={styles.main}>
        {/* 내비게이션 버튼 */}
        <div className={styles.nav}>
          <Link to="/transfer" className={styles.navBtn}>💸 송금하기</Link>
        </div>

        {/* 계좌 목록 섹션 */}
        <section className={styles.section}>
          <div className={styles.sectionHeader}>
            <h2 className={styles.sectionTitle}>내 계좌</h2>
            <button className={styles.addBtn} onClick={() => setShowModal(true)}>
              + 계좌 개설
            </button>
          </div>

          {error && <p className={styles.error}>{error}</p>}

          {loading ? (
            <div className={styles.loading}>로딩 중...</div>
          ) : accounts.length === 0 ? (
            <div className={styles.empty}>
              <p>보유한 계좌가 없습니다.</p>
              <p>계좌를 개설해 보세요!</p>
            </div>
          ) : (
            <div className={styles.accountGrid}>
              {accounts.map((account) => (
                <Link
                  to={`/accounts/${account.id}`}
                  key={account.id}
                  className={styles.accountCard}
                >
                  <div className={styles.accountNumber}>{account.accountNumber}</div>
                  <div className={styles.balance}>{formatBalance(account.balance)}</div>
                  <div className={styles.accountDate}>
                    개설일: {new Date(account.createdAt).toLocaleDateString('ko-KR')}
                  </div>
                </Link>
              ))}
            </div>
          )}
        </section>
      </main>

      {/* 계좌 개설 모달 */}
      {showModal && (
        <div className={styles.modalOverlay} onClick={() => setShowModal(false)}>
          <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
            <h3 className={styles.modalTitle}>계좌 개설</h3>
            <form onSubmit={handleCreateAccount}>
              <div className={styles.field}>
                <label className={styles.label}>초기 입금액 (원)</label>
                <input
                  className={styles.input}
                  type="number"
                  min="0"
                  step="1000"
                  value={initialBalance}
                  onChange={(e) => setInitialBalance(e.target.value)}
                  placeholder="0"
                  required
                />
              </div>
              <div className={styles.modalActions}>
                <button type="button" className={styles.cancelBtn} onClick={() => setShowModal(false)}>
                  취소
                </button>
                <button type="submit" className={styles.confirmBtn} disabled={creating}>
                  {creating ? '개설 중...' : '개설'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
