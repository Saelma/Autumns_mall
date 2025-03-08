// pages/api/naver/user.js
export default async function handler(req, res) {
    const { accessToken } = req.query;
  
    try {
      const response = await fetch('https://openapi.naver.com/v1/nid/me', {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${accessToken}`,
          'Content-Type': 'application/json',
        },
      });
  
      if (!response.ok) {
        return res.status(response.status).json({ error: '네이버에서 유저 정보를 불러오는 데 실패했습니다!' });
      }
  
      const data = await response.json();
      return res.status(200).json(data);
    } catch (error) {
      console.error('유저 정보를 불러오는 데 실패했습니다!:', error);
      return res.status(500).json({ error: '내부 서버 오류' });
    }
  }
  