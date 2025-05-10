import { useLocation } from 'react-router-dom';

const Home = () => {
  const location = useLocation();
  const { role } = location.state || { role: 'user' }; // fallback default

  return <h1>Welcome to {role} home page</h1>;
};

export default Home;
