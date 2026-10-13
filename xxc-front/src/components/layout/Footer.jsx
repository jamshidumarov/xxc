import { Link } from 'react-router-dom'
import { useLang } from '../../i18n/index.jsx'
import Icon from '../ui/Icon'

export default function Footer() {
  const { t } = useLang()
  const f = t.footer

  return (
    <footer style={{ background: '#1B2A2F', color: '#D5DFDB' }} className="mt-auto">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-10">
          <div>
            <h3 className="text-2xl font-bold mb-3" style={{ fontFamily: "'Playfair Display', serif", color: '#8FC1B5' }}>
              X&X Ceramics
            </h3>
            <p className="text-sm" style={{ color: '#9FB3B6', lineHeight: 1.7 }}>{f.desc}</p>
          </div>

          <div>
            <h4 className="font-semibold mb-4 text-sm uppercase tracking-wider" style={{ color: '#8FC1B5' }}>{f.pages}</h4>
            <ul className="space-y-2">
              {f.links.map(([to, label]) => (
                <li key={to}>
                  <Link to={to} className="text-sm transition-colors hover:text-[#8FC1B5]" style={{ color: '#9FB3B6' }}>{label}</Link>
                </li>
              ))}
            </ul>
          </div>

          <div>
            <h4 className="font-semibold mb-4 text-sm uppercase tracking-wider" style={{ color: '#8FC1B5' }}>{f.contactTitle}</h4>
            <ul className="space-y-3 text-sm" style={{ color: '#9FB3B6' }}>
              {t.contact.contactItems.map(c => (
                <li key={c.label} className="flex items-center gap-2.5">
                  <Icon name={c.icon} size={16} style={{ color: '#8FC1B5' }} />
                  {c.href ? (
                    <a href={c.href} target={c.href.startsWith('http') ? '_blank' : undefined} rel="noreferrer"
                      className="hover:text-[#8FC1B5] transition-colors">{c.value}</a>
                  ) : (
                    <span>{c.value}</span>
                  )}
                </li>
              ))}
            </ul>
          </div>
        </div>

        <div className="mt-10 pt-6 text-center text-xs" style={{ borderTop: '1px solid #243A40', color: '#8A9FA3' }}>
          © {new Date().getFullYear()} X&X Ceramics. {f.rights}
        </div>
      </div>
    </footer>
  )
}
