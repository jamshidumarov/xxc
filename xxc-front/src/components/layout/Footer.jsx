import { Link } from 'react-router-dom'
import { useLang } from '../../i18n/index.jsx'

export default function Footer() {
  const { t } = useLang()
  const f = t.footer

  return (
    <footer style={{ background: '#2C1810', color: '#E0D5C8' }} className="mt-auto">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-10">
          <div>
            <h3 className="text-2xl font-bold mb-3" style={{ fontFamily: "'Playfair Display', serif", color: '#C4986B' }}>
              X&X Ceramics
            </h3>
            <p className="text-sm" style={{ color: '#A08070', lineHeight: 1.7 }}>{f.desc}</p>
          </div>

          <div>
            <h4 className="font-semibold mb-4 text-sm uppercase tracking-wider" style={{ color: '#C4986B' }}>{f.pages}</h4>
            <ul className="space-y-2">
              {f.links.map(([to, label]) => (
                <li key={to}>
                  <Link to={to} className="text-sm transition-colors hover:text-[#C4986B]" style={{ color: '#A08070' }}>{label}</Link>
                </li>
              ))}
            </ul>
          </div>

          <div>
            <h4 className="font-semibold mb-4 text-sm uppercase tracking-wider" style={{ color: '#C4986B' }}>{f.contactTitle}</h4>
            <ul className="space-y-2 text-sm" style={{ color: '#A08070' }}>
              <li><a href="https://t.me/xxceramics" className="hover:text-[#C4986B] transition-colors">Telegram: @xxceramics</a></li>
              <li><a href="tel:+998901234567" className="hover:text-[#C4986B] transition-colors">+998 90 123 45 67</a></li>
              <li><a href="https://instagram.com/xx_ceramics" className="hover:text-[#C4986B] transition-colors">Instagram: @xx_ceramics</a></li>
              <li style={{ color: '#6B4A35' }}>Du–Sha, 09:00–18:00</li>
            </ul>
          </div>
        </div>

        <div className="mt-10 pt-6 text-center text-xs" style={{ borderTop: '1px solid #3D2415', color: '#6B4A35' }}>
          © {new Date().getFullYear()} X&X Ceramics. {f.rights}
        </div>
      </div>
    </footer>
  )
}
