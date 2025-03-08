/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  compiler: {
    styledComponents: true,
  },
  async rewrites() {
    return [
      {
        source: '/api/naver/user',  // 프론트엔드에서 호출할 경로
        destination: 'https://openapi.naver.com/v1/nid/me',  // 실제 네이버 API 경로
      },
    ];
  },
};

module.exports = nextConfig;