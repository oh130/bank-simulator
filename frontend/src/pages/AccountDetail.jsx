import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import api from '../api/axios';
import styles from './AccountDetail.module.css';

// 계좌 상세 페이지 - 잔액 및 거래 내역 조회
export default function AccountDetail() {
  const { id } = useParams();
  const [account, setAccount] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        // 계좌 정보와 거래 내역 병렬로 조회
        const [accountRes, txRes] = await Promise.all([
          api.get(`/accounts/${id}`),
          api.get(`/transactions/accounts/${id}`),
        ]);
        setAccount(accountRes.data.data);
        setTransactions(txRes.data.data);
      } catch (err) {
        setError('데이터를 불러오는 데 실패했습니다');
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [id]);

  const formatAmount = (amount) =>
    new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW' }).format(amount);

  const formatDate = (dateStr) =>
    new Date(dateStr).toLocaleString('ko-KR', {
      year: 'numeric', month: '2-digit', day: '2-digit',
      hour: '2-digit', minute: '2-digit',
    });

  // 거래 유형별 색상 구분
  const getTypeStyle = (type) => {
    if (type === 'DEPOSIT' || type === 'TRANSFER_IN') return styles.credit;
    return styles.debit;
  };

  // 거래 유형별 금액 부호 표시
  const getAmountSign = (type) =>
    (type === 'DEPOSIT' || type === 'TRANSFER_IN') ? '+' : '-';

  if (loading) return <div className={styles.loading}>로딩 중...</div>;
  if (error) return <div className={styles.error}>{error}</div>;

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <div className={styles.headerInner}>
          <Link to="/dashboard" className={styles.back}>← 대시보드</Link>
          <span className={styles.brand}>🏦 Bank Simulator</span>
        </div>
      </header>

      <main className={styles.main}>
        {/* 계좌 정보 카드 */}
        {account && (
          <div className={styles.accountCard}>
            <div className={styles.accountNumber}>{account.accountNumber}</div>
            <div className={styles.balanceLabel}>현재 잔액</div>
            <div className={styles.balance}>{formatAmount(account.balance)}</div>
            <div className={styles.openDate}>
              개설일: {new Date(account.createdAt).toLocaleDateString('ko-KR')}
            </div>
            <Link to="/transfer" className={styles.transferBtn}>
              💸 이 계좌에서 송금
            </Link>
          </div>
        )}

        {/* 거래 내역 목록 */}
        <section className={styles.section}>
          <h2 className={styles.sectionTitle}>거래 내역</h2>
          {transactions.length === 0 ? (
            <div className={styles.empty}>거래 내역이 없습니다</div>
          ) : (
            <ul className={styles.txList}>
              {transactions.map((tx) => (
                <li key={tx.id} className={styles.txItem}>
                  <div className={styles.txLeft}>
                    <span className={`${styles.txType} ${getTypeStyle(tx.type)}`}>
                      {tx.typeLabel}
                    </span>
                    <div className={styles.txDesc}>{tx.description}</div>
                    {tx.counterpartAccountNumber && (
                      <div className={styles.txCounterpart}>
                        상대 계좌: {tx.counterpartAccountNumber}
                      </div>
                    )}
                    <div className={styles.txDate}>{formatDate(tx.createdAt)}</div>
                  </div>
                  <div className={styles.txRight}>
                    <div className={`${styles.txAmount} ${getTypeStyle(tx.type)}`}>
                      {getAmountSign(tx.type)}{formatAmount(tx.amount)}
                    </div>
                    <div className={styles.txBalance}>
                      잔액 {formatAmount(tx.balanceAfter)}
                    </div>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </section>
      </main>
    </div>
  );
}
